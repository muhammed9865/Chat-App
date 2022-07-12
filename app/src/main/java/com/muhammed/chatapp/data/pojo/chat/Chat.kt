package com.muhammed.chatapp.data.pojo.chat

import com.muhammed.chatapp.data.pojo.message.Message

open class Chat(
    val cid: String = "",
    val messagesId: String = "",
    val createdSince: Long = System.currentTimeMillis(),
    val lastMessage: Message = Message(),
    val newMessagesCount: Int = 0
) {
    enum class TYPE {
        PRIVATE, GROUP
    }
}
