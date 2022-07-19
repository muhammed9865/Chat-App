package com.muhammed.chatapp.presentation.state

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient

sealed class AuthActivityState {
    object Idle : AuthActivityState()
    object AuthActivitySuccess : AuthActivityState()
    object ValidationSuccess : AuthActivityState()
    data class ValidationFailure(val validationState: ValidationState) : AuthActivityState()
    data class OnGoogleAuthStart(val client: GoogleSignInClient) : AuthActivityState()
    data class OnGoogleAuthSuccess(val client: GoogleSignInClient) : AuthActivityState()
    data class OnGoogleAuthFailure(val error: String) : AuthActivityState()
    data class AuthActivityFailure(val error: String) : AuthActivityState() {
        init {
            Log.e(TAG, error)
        }

        companion object {
            private const val TAG = "AuthActivityState"
        }
    }
}