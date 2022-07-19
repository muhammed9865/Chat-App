package com.muhammed.chatapp.presentation.state

import android.util.Log
import com.google.firebase.firestore.Query
import com.muhammed.chatapp.data.pojo.chat.PrivateChat

// Chats Fragment States
sealed class ChatsActivityState {
    object Idle : ChatsActivityState()
    object Loading : ChatsActivityState()

    /*
        On this state, show the Join group dialog
     */
    object FirstTime : ChatsActivityState()
    data class StartListeningToRooms(val roomsQuery: Query) : ChatsActivityState()
    data class UserExists(val privateChat: PrivateChat) : ChatsActivityState()
    data class PrivateRoomCreated(val room: PrivateChat) : ChatsActivityState()

    // Serialized to String to be sent as Intent Extra, Deserialize to MessagingRoom
    data class EnterChat(val chatAndRoom: String) : ChatsActivityState()
    object SignedOut : ChatsActivityState()
    data class Error(val error: Throwable) : ChatsActivityState() {
        init {
            Log.e(TAG, error.message.toString())
        }

        companion object {
            private const val TAG = "ChatsActivityState"
        }
    }
}
