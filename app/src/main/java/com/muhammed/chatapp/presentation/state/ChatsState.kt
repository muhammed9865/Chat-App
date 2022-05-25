package com.muhammed.chatapp.presentation.state

// Chats Fragment States
sealed class ChatsState {
    object Idle : ChatsState()
    object SignedOut : ChatsState()
    data class Error(val errorMessage: String): ChatsState()
}
