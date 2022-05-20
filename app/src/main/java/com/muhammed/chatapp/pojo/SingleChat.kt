package com.muhammed.chatapp.pojo

data class SingleChat(
    val id: Long,
    val profileName: String,
    val profilePicture: String,
    val lastMessageText: String,
    val lastMessageDate: Long,
    val newMessagesCount: Int
)
