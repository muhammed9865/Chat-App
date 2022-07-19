package com.muhammed.chatapp.presentation.state

sealed class CreateGroupDialogStates {
    object Idle : CreateGroupDialogStates()
    data class Failed(val error: String) : CreateGroupDialogStates()
    object Creating : CreateGroupDialogStates()
    object CreatedSuccessfully : CreateGroupDialogStates()


}