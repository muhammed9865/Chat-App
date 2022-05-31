package com.muhammed.chatapp.presentation.state

sealed class MessagingRoomStates {
    object Idle :MessagingRoomStates()
    data class Error(val error: String): MessagingRoomStates()
}