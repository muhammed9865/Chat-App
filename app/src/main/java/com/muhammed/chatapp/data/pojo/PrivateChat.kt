package com.muhammed.chatapp.data.pojo

import com.google.gson.Gson

data class PrivateChat(
    val cid: String = "",
    val firstUser: User = User(),
    val secondUser: User = User(),
    val messagesId: String = "",
    val createdSince: Long = System.currentTimeMillis(),
    val lastMessage: Message = Message(),
    val newMessagesCount: Int = 0
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
