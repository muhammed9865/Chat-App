package com.muhammed.chatapp.data.pojo.chat

import com.google.gson.Gson

data class MessagingRoom(
    val chatId: String = "",
    val messagesId: String = "",
    val title: String = "",
    val subTitle: String = "",
    val isJoined: Boolean = true,
    val isGroup: Boolean = false
) {
    override fun toString(): String {
        return Gson().toJson(this).toString()
    }

    companion object
}
