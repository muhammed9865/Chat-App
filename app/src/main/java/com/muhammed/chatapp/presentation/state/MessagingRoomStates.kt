package com.muhammed.chatapp.presentation.state

import android.util.Log
import com.muhammed.chatapp.data.pojo.chat.GroupChat

sealed class MessagingRoomStates {
    object Idle :MessagingRoomStates()
    // This state is used only when the user wants to check a group that he isn't member of's details
    data class ShowGroupDetails(val group: GroupChat) : MessagingRoomStates()
    data class Error(val error: String): MessagingRoomStates() {
        init {
            Log.e(TAG, error)
        }
        companion object {
            private const val TAG = "MessagingRoomStates"
        }
    }
}