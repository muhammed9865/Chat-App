package com.muhammed.chatapp.presentation.event

sealed class ValidationEvent {
    data class OnNicknameChanged(val name: String): ValidationEvent()
    data class OnEmailChanged(val email: String): ValidationEvent()
    data class OnPasswordChanged(val password: String): ValidationEvent()
    data class OnRepeatedPassword(val password: String): ValidationEvent()
    object Submit : ValidationEvent()
}
