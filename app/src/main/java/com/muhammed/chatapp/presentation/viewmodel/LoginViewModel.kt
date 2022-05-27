package com.muhammed.chatapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.muhammed.chatapp.data.repository.AuthRepository
import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.data.GoogleAuth
import com.muhammed.chatapp.data.GoogleAuthCallback
import com.muhammed.chatapp.data.repository.FirestoreRepository
import com.muhammed.chatapp.domain.use_cases.ValidateEmail
import com.muhammed.chatapp.domain.use_cases.ValidatePassword
import com.muhammed.chatapp.presentation.event.AuthenticationEvent
import com.muhammed.chatapp.presentation.state.AuthenticationState
import com.muhammed.chatapp.presentation.state.ValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FirestoreRepository,
    private val googleAuth: GoogleAuth,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
) : ViewModel() {
    private val _validation = MutableStateFlow(ValidationState())

    private val _authStates = Channel<AuthenticationState>()
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
                googleAuth.onTaskResult(event.data)
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

                sendState(AuthenticationState.ValidationFailure(newState))
            } else {
                loginUser()
            }
        }
    }

    private fun loginInInstantly() {
        val currentUser = authRepository.getCurrentUser()
        currentUser?.let {
            if (it.isEmailVerified) {
                viewModelScope.launch(Dispatchers.IO) {
                    val user = fireStoreRepository.getUser(it.email!!).values.first()
                    authRepository.saveCurrentUserDetails(user.email, FirestoreManager.Collections.USERS, user.nickname)
                    sendState(AuthenticationState.AuthenticationSuccess)
                }
            }
        }
    }


    private fun sendState(state: AuthenticationState) {
        viewModelScope.launch {
            _authStates.send(state)
        }
    }

    private fun loginUser() {
        val email = _validation.value.email
        val password = _validation.value.password

        viewModelScope.launch(Dispatchers.IO) {
            try {
                authRepository.loginUser(email, password)?.let {
                    if (it.isEmailVerified) {
                        val user = fireStoreRepository.getUser(it.email!!).values.first()
                        authRepository.saveCurrentUserDetails(user.email, FirestoreManager.Collections.USERS, user.nickname)
                        _authStates.send(AuthenticationState.AuthenticationSuccess)
                        return@launch
                    }
                    throw Exception("Either email or password is incorrect")
                }

            } catch (e: Exception) {
                _authStates.send(AuthenticationState.AuthenticationFailure(e.message.toString()))
            }

        }

    }

    private fun initGoogleAuthListener() {
        googleAuth.registerCallbackListener(object : GoogleAuthCallback.ViewModel {
            override fun onSigningStart(client: GoogleSignInClient) {
                sendState(AuthenticationState.OnGoogleAuthStart(client))
            }

            override fun onSigningSuccess(
                client: GoogleSignInClient,
                account: GoogleSignInAccount
            ) {

                account.email?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val user = authRepository.authenticateGoogleUser(it)
                            user?.let {
                                // Saving the User Details to later usage.
                                authRepository.saveCurrentUserDetails(
                                    it.email,
                                    FirestoreManager.Collections.GOOGLE_USERS,
                                    it.nickname
                                )

                                sendState(AuthenticationState.AuthenticationSuccess)
                            }
                                ?: throw Exception("User not found")
                        } catch (e: Exception) {
                            Log.d("LoginViewModel", "onSigningSuccess: ${e.message.toString()}")
                            sendState(AuthenticationState.AuthenticationFailure(e.message!!))
                        }


                    }
                }
                sendState(AuthenticationState.OnGoogleAuthSuccess(client))
            }

            override fun onSigningFailure(error: String?) {
                sendState(
                    AuthenticationState.OnGoogleAuthFailure(
                        error ?: "Google Auth Failed"
                    )
                )

            }
        })
    }

}