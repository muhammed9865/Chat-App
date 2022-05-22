package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.muhammed.chatapp.data.GoogleAuth
import com.muhammed.chatapp.data.GoogleAuthCallback
import com.muhammed.chatapp.domain.AuthRepository
import com.muhammed.chatapp.domain.Callbacks
import com.muhammed.chatapp.domain.validation.ValidateEmail
import com.muhammed.chatapp.domain.validation.ValidatePassword
import com.muhammed.chatapp.pojo.User
import com.muhammed.chatapp.presentation.event.AuthenticationEvent
import com.muhammed.chatapp.presentation.state.AuthenticationState
import com.muhammed.chatapp.presentation.state.ValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val googleAuth: GoogleAuth,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
) : ViewModel() {
    private val _validation = MutableStateFlow(ValidationState())
    val validation = _validation.asStateFlow()

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
                googleAuth.signIn()
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
            sendState(AuthenticationState.AuthenticationSuccess)
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

        authRepository.loginUser(
            email,
            password,
            object : Callbacks.AuthCompleteListener {
                override fun onSuccess(user: User, token: String?) {
                    sendState(AuthenticationState.AuthenticationSuccess)
                }

                override fun onFailure(message: String) {
                    viewModelScope.launch {
                        sendState(AuthenticationState.AuthenticationFailure(message))
                    }
                }
            })
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
                account.email?.let { doOnEvent(AuthenticationEvent.OnEmailChanged(it)) }
                account.displayName?.let { doOnEvent(AuthenticationEvent.OnNicknameChanged(it)) }
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