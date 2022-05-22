package com.muhammed.chatapp.presentation.event

import android.content.Intent

sealed class AuthenticationEvent {
    data class OnNicknameChanged(val name: String): AuthenticationEvent()
    data class OnEmailChanged(val email: String): AuthenticationEvent()
    data class OnPasswordChanged(val password: String): AuthenticationEvent()
    data class OnRepeatedPassword(val password: String): AuthenticationEvent()
    object StartGoogleAuthentication : AuthenticationEvent()
    data class OnGoogleCredentialsAvailable(val data: Intent): AuthenticationEvent()
    object LoginWithSavedToken : AuthenticationEvent()
    object Submit : AuthenticationEvent()
}
