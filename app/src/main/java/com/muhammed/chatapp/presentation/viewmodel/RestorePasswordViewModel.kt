package com.muhammed.chatapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.chatapp.data.repository.AuthRepository
import com.muhammed.chatapp.domain.use_cases.ValidateEmail
import com.muhammed.chatapp.presentation.state.RestorePasswordStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestorePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val validateEmail: ValidateEmail
) : ViewModel() {

    private val _states = MutableStateFlow<RestorePasswordStates>(RestorePasswordStates.Idle)
    val state = _states.asStateFlow()

    var email: String = ""


    fun reset() {
        validateEmail()
    }

    private fun validateEmail() {
        val result = validateEmail.execute(email)

        if (result.isSuccessful) {
            resetPassword()
        } else {
            _states.value = RestorePasswordStates.InvalidEmail(result.errorMessage!!)
        }

    }

    private fun resetPassword() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _states.value = RestorePasswordStates.SendingRestoreEmail
                authRepository.resetPassword(email)
                _states.value = RestorePasswordStates.EmailSentSuccessfully
            } catch (e: Exception) {
                Log.e("RestorePassword", "ViewModel: ${e.message}")
                _states.value = RestorePasswordStates.Error(e.message.toString())
            }
        }
    }
}
