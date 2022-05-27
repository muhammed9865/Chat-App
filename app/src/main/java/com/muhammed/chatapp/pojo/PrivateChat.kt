package com.muhammed.chatapp.pojo

data class PrivateChat(
    val cid: String = "",
    val firstUser: User = User(),
    val secondUser: User = User(),
    val messagesId: String = "",
    val createdSince: Long = System.currentTimeMillis(),
    val lastMessageText: String = "",
    val lastMessageDate: Long = System.currentTimeMillis(),
    val newMessagesCount: Int = 0
)
