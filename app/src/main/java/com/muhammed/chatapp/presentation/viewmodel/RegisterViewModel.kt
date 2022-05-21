package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.chatapp.domain.AuthRepository
import com.muhammed.chatapp.domain.Callbacks
import com.muhammed.chatapp.domain.validation.ValidateEmail
import com.muhammed.chatapp.domain.validation.ValidateNickname
import com.muhammed.chatapp.domain.validation.ValidatePassword
import com.muhammed.chatapp.domain.validation.ValidateRepeatedPassword
import com.muhammed.chatapp.pojo.User
import com.muhammed.chatapp.presentation.event.ValidationEvent
import com.muhammed.chatapp.presentation.state.RegistrationState
import com.muhammed.chatapp.presentation.state.ValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateNickname: ValidateNickname,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateRepeatedPassword: ValidateRepeatedPassword,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _states = MutableStateFlow(ValidationState())


    private val _registrationChannel = Channel<RegistrationState>()
    val registrationChannel = _registrationChannel.receiveAsFlow()

    fun doOnEvent(event: ValidationEvent) {
        when(event) {
            is ValidationEvent.OnNicknameChanged -> {
                _states.value = _states.value.copy(nickname = event.name)
            }

            is ValidationEvent.OnEmailChanged -> {
                _states.value = _states.value.copy(email = event.email)
            }

            is ValidationEvent.OnPasswordChanged -> {
                _states.value = _states.value.copy(password = event.password)
            }

            is ValidationEvent.OnRepeatedPassword -> {
                _states.value = _states.value.copy(repeatedPassword = event.password)
            }

            is ValidationEvent.Submit -> {
                validate()
            }
        }
    }

    private fun validate() {
        viewModelScope.launch(Dispatchers.IO) {
            // Validating Inputs
            val nicknameResult = validateNickname.execute(_states.value.nickname)
            val emailResult = validateEmail.execute(_states.value.email)
            val passwordResult = validatePassword.execute(_states.value.password)
            val repeatedPasswordResult =
                validateRepeatedPassword.execute(_states.value.password, _states.value.repeatedPassword)

            // Result errors if any.
            val hasErrors =
                listOf(emailResult, passwordResult, repeatedPasswordResult, nicknameResult).any {
                    !it.isSuccessful
                }
            // If there are no errors, register user.
            if (hasErrors) {
                val newState = _states.value.copy(
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                    repeatedPasswordError = repeatedPasswordResult.errorMessage,
                    nicknameError = nicknameResult.errorMessage
                )
                _registrationChannel.send(RegistrationState.ValidationFailure(newState))
            }else {
                registerUser()
            }
        }
    }

    private  fun registerUser() {
        val nickName = _states.value.nickname
        val email = _states.value.email
        val password = _states.value.password

        authRepository.registerUser(nickName, email, password, object : Callbacks.AuthCompleteListener {
            override fun onSuccess(user: User) {
                viewModelScope.launch {
                    _registrationChannel.send(RegistrationState.RegistrationSuccess)
                    authRepository.saveUserOnFirestore(user)
                }
            }

            override fun onFailure(message: String) {
                viewModelScope.launch {
                    _registrationChannel.send(RegistrationState.RegistrationFailure(message))
                }
            }
        })
    }


}