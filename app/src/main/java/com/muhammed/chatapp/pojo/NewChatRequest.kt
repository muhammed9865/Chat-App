package com.muhammed.chatapp.pojo

data class NewChatRequest(
    val currentUserEmail: String,
    val currentUserCategory: String,
    val otherUserEmail: String,
)
