package com.muhammed.chatapp.presentation.event

import com.muhammed.chatapp.data.pojo.MessagingRoom

sealed class ChatsEvent {
    object Idle : ChatsEvent()
    object SignOut : ChatsEvent()

    data class CreatePrivateRoom(val email: String): ChatsEvent()
    data class JoinPrivateChat(val chat: MessagingRoom): ChatsEvent()
}