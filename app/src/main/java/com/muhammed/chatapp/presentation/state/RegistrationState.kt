package com.muhammed.chatapp.presentation.state

sealed class RegistrationState {

    object RegistrationSuccess: RegistrationState()
    object ValidationSuccess: RegistrationState()
    data class ValidationFailure(val validationState: ValidationState): RegistrationState()
    data class RegistrationFailure(val error: String): RegistrationState()
}