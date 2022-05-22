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
import com.muhammed.chatapp.presentation.event.RegisterEvent
import com.muhammed.chatapp.presentation.state.RegistrationState
import com.muhammed.chatapp.presentation.state.ValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _validationStates = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val validationStates = _validationStates.asStateFlow()
    private val _validation = MutableStateFlow(ValidationState())
    val validation = _validation.asStateFlow()

    init {
        prepareGoogleAuthListener()
    }

    fun doOnEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.OnNicknameChanged -> {
                _validation.value = _validation.value.copy(nickname = event.name)
            }

            is RegisterEvent.OnEmailChanged -> {
                _validation.value = _validation.value.copy(email = event.email)
            }

            is RegisterEvent.OnPasswordChanged -> {
                _validation.value = _validation.value.copy(password = event.password)
            }

            is RegisterEvent.OnRepeatedPassword -> {
                _validation.value = _validation.value.copy(repeatedPassword = event.password)
            }

            is RegisterEvent.StartGoogleAuthentication -> {
                googleAuth.signIn()
            }

            is RegisterEvent.OnGoogleCredentialsAvailable -> {
                googleAuth.onTaskResult(event.data)
            }

            is RegisterEvent.Submit -> {
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
                _validationStates.value = RegistrationState.ValidationFailure(newState)
            }else {
                registerUser()
            }
        }
    }

    private fun prepareGoogleAuthListener() {
            googleAuth.registerCallbackListener(object : GoogleAuthCallback.ViewModel {
                override fun onSigningStart(client: GoogleSignInClient) {
                    viewModelScope.launch {
                        _validationStates.value = RegistrationState.OnGoogleAuthStart(client)
                    }
                }

                override fun onSigningSuccess(client: GoogleSignInClient, account: GoogleSignInAccount) {
                    account.email?.let { doOnEvent(RegisterEvent.OnEmailChanged(it)) }
                    account.displayName?.let { doOnEvent(RegisterEvent.OnNicknameChanged(it)) }
                    viewModelScope.launch {
                        _validationStates.value = RegistrationState.OnGoogleAuthSuccess(client)
                    }
                }

                override fun onSigningFailure(error: String?) {
                    viewModelScope.launch {
                        _validationStates.value = RegistrationState.OnGoogleAuthFailure(error ?: "Google Auth Failed")
                    }
                }
            })
    }

    private  fun registerUser() {
        val nickName = _validation.value.nickname
        val email = _validation.value.email
        val password = _validation.value.password

        authRepository.registerUser(nickName, email, password, object : Callbacks.AuthCompleteListener {
            override fun onSuccess(user: User) {
                Log.d(TAG, "onSuccess: $user")
                viewModelScope.launch(Dispatchers.IO) {
                    _validationStates.value = RegistrationState.RegistrationSuccess
                    authRepository.saveUserOnFirestore(user).collect {
                        Log.d(TAG, "onSuccess: ${it.errorMessage}")
                    }
                }
            }

            override fun onFailure(message: String) {
                viewModelScope.launch {
                    _validationStates.value = RegistrationState.RegistrationFailure(message)
                }
            }
        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }


}