package com.muhammed.chatapp.presentation.event

import com.muhammed.chatapp.data.pojo.MessagingRoom

sealed class ChatsEvent {
    object Idle : ChatsEvent()
    // should change the user isFirstTime status to false
    // Upon it many things won't show up
    object SetFirstTimeToFalse : ChatsEvent()
    object SignOut : ChatsEvent()

    data class CreatePrivateRoom(val email: String): ChatsEvent()
    data class JoinPrivateChat(val chat: MessagingRoom): ChatsEvent()
}