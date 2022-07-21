package com.muhammed.chatapp.presentation.state

sealed class RestorePasswordStates {
    object Idle : RestorePasswordStates()
    data class InvalidEmail(val error: String) : RestorePasswordStates()
    object SendingRestoreEmail : RestorePasswordStates()
    object EmailSentSuccessfully : RestorePasswordStates()
    data class Error(val error: String) : RestorePasswordStates()

}