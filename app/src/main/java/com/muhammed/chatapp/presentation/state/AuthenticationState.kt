package com.muhammed.chatapp.presentation.state

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient

sealed class AuthenticationState {
    object Idle: AuthenticationState()
    object AuthenticationSuccess: AuthenticationState()
    object ValidationSuccess: AuthenticationState()
    data class ValidationFailure(val validationState: ValidationState): AuthenticationState()
    data class OnGoogleAuthStart(val client: GoogleSignInClient): AuthenticationState()
    data class OnGoogleAuthSuccess(val client: GoogleSignInClient) : AuthenticationState()
    data class OnGoogleAuthFailure(val error: String): AuthenticationState()
    data class AuthenticationFailure(val error: String): AuthenticationState() {
        init {
            Log.e(TAG, error)
        }
        companion object {
            private const val TAG = "AuthenticationState"
        }
    }
}