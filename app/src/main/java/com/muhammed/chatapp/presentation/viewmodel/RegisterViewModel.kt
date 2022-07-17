package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.messaging.FirebaseMessaging
import com.muhammed.chatapp.data.implementation.network.FirestoreNetworkDatabaseImp
import com.muhammed.chatapp.data.implementation.network.GoogleAuth
import com.muhammed.chatapp.data.implementation.network.GoogleAuthCallback
import com.muhammed.chatapp.data.pojo.user.User
import com.muhammed.chatapp.data.repository.AuthRepository
import com.muhammed.chatapp.domain.use_cases.ValidateEmail
import com.muhammed.chatapp.domain.use_cases.ValidateNickname
import com.muhammed.chatapp.domain.use_cases.ValidatePassword
import com.muhammed.chatapp.domain.use_cases.ValidateRepeatedPassword
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
import kotlinx.coroutines.tasks.await
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
        when (event) {
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
                viewModelScope.launch(Dispatchers.IO) {
                    googleAuth.signIn()
                }
            }

            is AuthenticationEvent.OnGoogleCredentialsAvailable -> {
                viewModelScope.launch {
                    googleAuth.onTaskResult(event.data)
                }
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
            val nicknameResult = validateNickname.execute(_validation.value.nickname)
            val emailResult = validateEmail.execute(_validation.value.email)
            val passwordResult = validatePassword.execute(_validation.value.password)
            val repeatedPasswordResult =
                validateRepeatedPassword.execute(
                    _validation.value.password,
                    _validation.value.repeatedPassword
                )

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
            } else {
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

            override suspend fun onSigningSuccess(
                client: GoogleSignInClient,
                account: GoogleSignInAccount
            ) {
                account.email?.let { doOnEvent(AuthenticationEvent.OnEmailChanged(it)) }
                account.displayName?.let { doOnEvent(AuthenticationEvent.OnNicknameChanged(it)) }
                val token = FirebaseMessaging.getInstance().token.await()

                val user = User(
                    account.id ?: "",
                    account.displayName ?: "",
                    account.email ?: "",
                    "",
                    collection = FirestoreNetworkDatabaseImp.Collections.GOOGLE_USERS,
                    token = token
                )

                viewModelScope.launch {
                    _authStates.send(
                        try {
                            authRepository.saveGoogleUser(user)
                            AuthenticationState.AuthenticationSuccess
                        } catch (e: Exception) {
                            AuthenticationState.AuthenticationFailure(e.message!!)
                        }
                    )
                }
            }

            override fun onSigningFailure(error: String?) {
                viewModelScope.launch {
                    _authStates.send(
                        AuthenticationState.OnGoogleAuthFailure(
                            error ?: "Google Auth Failed"
                        )
                    )
                }
            }
        })
    }

    // Register user, then save user credentials on Firestore.
    private fun registerUser() {
        val nickName = _validation.value.nickname
        val email = _validation.value.email
        val password = _validation.value.password

        viewModelScope.launch(Dispatchers.IO) {
            try {
                authRepository.registerUser(nickName, email, password)
                _authStates.send(AuthenticationState.AuthenticationSuccess)
            } catch (e: Exception) {
                _authStates.send(AuthenticationState.AuthenticationFailure(e.message.toString()))
            }
        }


    }

    companion object {
        @Suppress("unused")
        private const val TAG = "RegisterViewModel"
    }


}