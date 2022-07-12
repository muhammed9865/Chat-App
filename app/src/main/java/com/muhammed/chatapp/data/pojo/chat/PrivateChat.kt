package com.muhammed.chatapp.data.pojo.chat

import com.google.gson.Gson
import com.muhammed.chatapp.data.pojo.message.Message
import com.muhammed.chatapp.data.pojo.user.User

class PrivateChat(
    cid: String = "",
    messagesId: String = "",
    val firstUser: User = User(),
    val secondUser: User = User(),
): Chat(cid, messagesId) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
