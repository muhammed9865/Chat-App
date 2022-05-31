package com.muhammed.chatapp.data.pojo

data class Messages(
    val status: ChatStatus = ChatStatus(),
    val messages: List<Message> = emptyList()
)
