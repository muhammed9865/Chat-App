package com.muhammed.chatapp.pojo

data class PrivateChat(
    val cid: String = "",
    val profileName: String = "",
    val profilePicture: String = "",
    val messagesId: String = "",
    val createdSince: Long = System.currentTimeMillis(),
    val lastMessageText: String = "",
    val lastMessageDate: Long = System.currentTimeMillis(),
    val newMessagesCount: Int = 0
)
