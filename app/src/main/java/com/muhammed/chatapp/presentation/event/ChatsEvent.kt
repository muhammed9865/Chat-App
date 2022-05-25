package com.muhammed.chatapp.presentation.event

sealed class ChatsEvent {
    object SignOut : ChatsEvent()
}