package com.muhammed.chatapp.presentation.state

import com.google.android.gms.auth.api.signin.GoogleSignInClient

sealed class RegistrationState {

    object RegistrationSuccess: RegistrationState()
    object ValidationSuccess: RegistrationState()
    data class ValidationFailure(val validationState: ValidationState): RegistrationState()
    data class OnGoogleAuthStart(val client: GoogleSignInClient): RegistrationState()
    data class OnGoogleAuthSuccess(val client: GoogleSignInClient) : RegistrationState()
    data class OnGoogleAuthFailure(val error: String): RegistrationState()
    data class RegistrationFailure(val error: String): RegistrationState()
}