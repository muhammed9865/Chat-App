package com.muhammed.chatapp.presentation.state

import android.util.Log

sealed class MessagingRoomStates {
    object Idle :MessagingRoomStates()
    data class Error(val error: String): MessagingRoomStates() {
        init {
            Log.e(TAG, error)
        }
        companion object {
            private const val TAG = "MessagingRoomStates"
        }
    }
}