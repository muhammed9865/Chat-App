package com.muhammed.chatapp.presentation.event

import android.content.Intent

sealed class RegisterEvent {
    data class OnNicknameChanged(val name: String): RegisterEvent()
    data class OnEmailChanged(val email: String): RegisterEvent()
    data class OnPasswordChanged(val password: String): RegisterEvent()
    data class OnRepeatedPassword(val password: String): RegisterEvent()
    object StartGoogleAuthentication : RegisterEvent()
    data class OnGoogleCredentialsAvailable(val data: Intent): RegisterEvent()
    object Submit : RegisterEvent()
}
