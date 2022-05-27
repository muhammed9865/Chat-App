package com.muhammed.chatapp.presentation.state

import com.muhammed.chatapp.pojo.PrivateChat

// Chats Fragment States
sealed class ChatsState {
    object Idle : ChatsState()
    object Loading : ChatsState()
    data class ChatsListLoaded(val chats: List<PrivateChat>): ChatsState()
    object PrivateRoomCreated: ChatsState()
    object SignedOut : ChatsState()
    data class Error(val errorMessage: String): ChatsState()
}
