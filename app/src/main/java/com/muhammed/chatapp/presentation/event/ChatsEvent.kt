package com.muhammed.chatapp.presentation.event

sealed class ChatsEvent {
    object Idle : ChatsEvent()
    object SignOut : ChatsEvent()
    object LoadChats: ChatsEvent()
    object ListenForChats : ChatsEvent()
    data class CreatePrivateRoom(val email: String): ChatsEvent()
    data class JoinChat(val chatEmail: String): ChatsEvent()
    data class SendMessage(val message: String): ChatsEvent()
}