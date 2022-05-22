package com.muhammed.chatapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.muhammed.chatapp.data.GoogleAuth
import com.muhammed.chatapp.data.GoogleAuthCallback
import com.muhammed.chatapp.domain.AuthRepository
import com.muhammed.chatapp.domain.Callbacks
import com.muhammed.chatapp.domain.validation.ValidateEmail
import com.muhammed.chatapp.domain.validation.ValidateNickname
import com.muhammed.chatapp.domain.validation.ValidatePassword
import com.muhammed.chatapp.domain.validation.ValidateRepeatedPassword
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
class RegisterViewModel @Inject constructor(
    private val validateNickname: ValidateNickname,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateRepeatedPassword: ValidateRepeatedPassword,
    private val authRepository: AuthRepository,
    private val googleAuth: GoogleAuth
) : ViewModel() {

    private val _authStates = Channel<AuthenticationState>()
    val authStates = _authStates.receiveAsFlow()
    private val _validation = MutableStateFlow(ValidationState())
    val validation = _validation.asStateFlow()

    init {
        prepareGoogleAuthListener()
    }

    fun doOnEvent(event: AuthenticationEvent) {
        when(event) {
            is AuthenticationEvent.OnNicknameChanged -> {
                _validation.value = _validation.value.copy(nickname = event.name)
            }

            is AuthenticationEvent.OnEmailChanged -> {
                _validation.value = _validation.value.copy(email = event.email)
            }

            is AuthenticationEvent.OnPasswordChanged -> {
                _validation.value = _validation.value.copy(password = event.password)
            }

            is AuthenticationEvent.OnRepeatedPassword -> {
                _validation.value = _validation.value.copy(repeatedPassword = event.password)
            }

            is AuthenticationEvent.StartGoogleAuthentication -> {
                googleAuth.signIn()
            }

            is AuthenticationEvent.OnGoogleCredentialsAvailable -> {
                googleAuth.onTaskResult(event.data)
            }

            is AuthenticationEvent.Submit -> {
                validate()
            }
        }
    }

    private fun validate() {
        viewModelScope.launch(Dispatchers.IO) {
            // Validating Inputs
            val nicknameResult = validateNickname.execute(_validation.value.nickname)
            val emailResult = validateEmail.execute(_validation.value.email)
            val passwordResult = validatePassword.execute(_validation.value.password)
            val repeatedPasswordResult =
                validateRepeatedPassword.execute(_validation.value.password, _validation.value.repeatedPassword)

            // Result errors if any.
            val hasErrors =
                listOf(emailResult, passwordResult, repeatedPasswordResult, nicknameResult).any {
                    !it.isSuccessful
                }
            // If there are no errors, register user.
            if (hasErrors) {
                val newState = _validation.value.copy(
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                    repeatedPasswordError = repeatedPasswordResult.errorMessage,
                    nicknameError = nicknameResult.errorMessage
                )
                _authStates.send(AuthenticationState.ValidationFailure(newState))
            }else {
                registerUser()
            }
        }
    }

    private fun prepareGoogleAuthListener() {
            googleAuth.registerCallbackListener(object : GoogleAuthCallback.ViewModel {
                override fun onSigningStart(client: GoogleSignInClient) {
                    viewModelScope.launch {
                        _authStates.send(AuthenticationState.OnGoogleAuthStart(client))
                    }
                }

                override fun onSigningSuccess(client: GoogleSignInClient, account: GoogleSignInAccount) {
                    account.email?.let { doOnEvent(AuthenticationEvent.OnEmailChanged(it)) }
                    account.displayName?.let { doOnEvent(AuthenticationEvent.OnNicknameChanged(it)) }
                    viewModelScope.launch {
                        _authStates.send(AuthenticationState.OnGoogleAuthSuccess(client))
                    }
                }

                override fun onSigningFailure(error: String?) {
                    viewModelScope.launch {
                        _authStates.send(AuthenticationState.OnGoogleAuthFailure(error ?: "Google Auth Failed"))
                    }
                }
            })
    }

    // Register user, then save user credentials on Firestore.
    private  fun registerUser() {
        val nickName = _validation.value.nickname
        val email = _validation.value.email
        val password = _validation.value.password

        authRepository.registerUser(nickName, email, password, object : Callbacks.AuthCompleteListener {
            override fun onSuccess(user: User) {
                Log.d(TAG, "onSuccess: $user")
                viewModelScope.launch(Dispatchers.IO) {
                    _authStates.send(AuthenticationState.AuthenticationSuccess)
                    authRepository.saveUserOnFirestore(user).collect {
                        Log.d(TAG, "onSuccess: ${it.errorMessage}")
                    }
                }
            }

            override fun onFailure(message: String) {
                viewModelScope.launch {
                    _authStates.send(AuthenticationState.AuthenticationFailure(message))
                }
            }
        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }


}