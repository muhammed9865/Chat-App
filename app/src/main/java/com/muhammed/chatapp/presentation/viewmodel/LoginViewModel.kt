package com.muhammed.chatapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.muhammed.chatapp.data.implementation.network.GoogleAuth
import com.muhammed.chatapp.data.implementation.network.GoogleAuthCallback
import com.muhammed.chatapp.data.repository.AuthRepository
import com.muhammed.chatapp.data.repository.UserRepository
import com.muhammed.chatapp.domain.use_cases.ValidateEmail
import com.muhammed.chatapp.domain.use_cases.ValidatePassword
import com.muhammed.chatapp.presentation.event.AuthenticationEvent
import com.muhammed.chatapp.presentation.state.AuthActivityState
import com.muhammed.chatapp.presentation.state.ValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val googleAuth: GoogleAuth,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
) : ViewModel() {
    private val _validation = MutableStateFlow(ValidationState())

    private val _authStates = Channel<AuthActivityState>()
    val authStates = _authStates.receiveAsFlow()


    init {
        initGoogleAuthListener()

    }

    fun doOnEvent(event: AuthenticationEvent) {
        when (event) {
            is AuthenticationEvent.OnEmailChanged -> {
                _validation.value = _validation.value.copy(email = event.email)
            }

            is AuthenticationEvent.OnPasswordChanged -> {
                _validation.value = _validation.value.copy(password = event.password)
            }


            is AuthenticationEvent.StartGoogleAuthentication -> {
                viewModelScope.launch(Dispatchers.IO) {
                    googleAuth.signIn()
                }
            }

            is AuthenticationEvent.OnGoogleCredentialsAvailable -> {
                viewModelScope.launch {
                    googleAuth.onTaskResult(event.data)
                }
            }

            is AuthenticationEvent.LoginWithSavedToken -> {
                loginInInstantly()
            }

            is AuthenticationEvent.Submit -> {
                validate()
            }

            else -> {}
        }
    }

    private fun validate() {
        viewModelScope.launch(Dispatchers.IO) {
            // Validating Inputs
            val emailResult = validateEmail.execute(_validation.value.email)
            val passwordResult = validatePassword.execute(_validation.value.password)

            // Result errors if any.
            val hasErrors =
                listOf(emailResult, passwordResult).any {
                    !it.isSuccessful
                }
            // If there are no errors, register user.
            if (hasErrors) {
                val newState = _validation.value.copy(
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                )

                sendState(AuthActivityState.ValidationFailure(newState))
            } else {
                loginUser()
            }
        }
    }

    private fun loginInInstantly() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.currentUser.filterNotNull().collect {
                sendState(AuthActivityState.AuthActivitySuccess)
            }
        }

    }


    private fun sendState(state: AuthActivityState) {
        viewModelScope.launch {
            _authStates.send(state)
        }
    }

    private fun loginUser() {
        val email = _validation.value.email
        val password = _validation.value.password
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _authStates.send(AuthActivityState.EmailValid)
                authRepository.loginUser(email, password)
                _authStates.send(AuthActivityState.AuthActivitySuccess)
            } catch (e: Exception) {
                _authStates.send(AuthActivityState.AuthActivityFailure(e.message.toString()))
            }

        }

    }

    private fun initGoogleAuthListener() {
        googleAuth.registerCallbackListener(object : GoogleAuthCallback.ViewModel {
            override fun onSigningStart(client: GoogleSignInClient) {
                sendState(AuthActivityState.OnGoogleAuthStart(client))
            }

            override suspend fun onSigningSuccess(
                client: GoogleSignInClient,
                account: GoogleSignInAccount
            ) {

                account.email?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val user = authRepository.authenticateGoogleUser(it)
                            user?.let {
                                // Saving the User Details to later usage.
                                userRepository.saveUserDetails(user)
                                sendState(AuthActivityState.AuthActivitySuccess)
                            } ?: throw Exception("User not found")
                        } catch (e: Exception) {
                            Log.d("LoginViewModel", "onSigningSuccess: ${e.message.toString()}")
                            sendState(AuthActivityState.AuthActivityFailure(e.message!!))
                        }


                    }
                }
                sendState(AuthActivityState.OnGoogleAuthSuccess(client))
            }

            override fun onSigningFailure(error: String?) {
                sendState(
                    AuthActivityState.OnGoogleAuthFailure(
                        error ?: "Google Auth Failed"
                    )
                )

            }
        })
    }

}