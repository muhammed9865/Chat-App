package com.muhammed.chatapp.presentation.state

import com.google.firebase.firestore.Query
import com.muhammed.chatapp.pojo.PrivateChat
import com.muhammed.chatapp.pojo.User

// Chats Fragment States
sealed class ChatsState {
    object Idle : ChatsState()
    object Loading : ChatsState()
    data class ChatsListLoaded(val currentUser: User): ChatsState()
    data class StartListeningToRooms(val roomsQuery: Query): ChatsState()
    data class UserExists(val privateChat: PrivateChat) : ChatsState()
    object PrivateRoomCreated: ChatsState()
    object SignedOut : ChatsState()
    data class Error(val errorMessage: String): ChatsState()
}
