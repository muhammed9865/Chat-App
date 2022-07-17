package com.muhammed.chatapp.presentation.event

import android.content.Intent
import com.muhammed.chatapp.data.pojo.chat.GroupChat

sealed class MessagingRoomEvents {
    // Whenever entering the ChatRoomActivity, send the Intent to viewModel and it will respond with a MessagingRoom object to update UI.
    data class SendUserDetails(val intent: Intent?) : MessagingRoomEvents()

    data class JoinGroup(val group: GroupChat) : MessagingRoomEvents()

    data class SendMessage(val message: String) : MessagingRoomEvents()
}
