package com.muhammed.chatapp.presentation.state

import android.util.Log
import com.google.firebase.firestore.Query
import com.muhammed.chatapp.data.pojo.chat.PrivateChat

// Chats Fragment States
sealed class ChatsState {
    object Idle : ChatsState()
    object Loading : ChatsState()
    /*
        On this state, show the Join group dialog
     */
    object FirstTime : ChatsState()
    data class StartListeningToRooms(val roomsQuery: Query): ChatsState()
    data class UserExists(val privateChat: PrivateChat) : ChatsState()
    data class PrivateRoomCreated(val room: PrivateChat): ChatsState()
    // Serialized to String to be sent as Intent Extra, Deserialize to MessagingRoom
    data class EnterChat(val chatAndRoom: String) : ChatsState()
    object SignedOut : ChatsState()
    data class Error(val error: Throwable) : ChatsState() {
        init {
            Log.e(TAG, error.message.toString())
        }

        companion object {
            private const val TAG = "ChatsState"
        }
    }
}
