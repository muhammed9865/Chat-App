package com.muhammed.chatapp.data.pojo.message

import com.muhammed.chatapp.data.pojo.ChatStatus

data class Messages(
    val status: ChatStatus = ChatStatus(),
    val messages: List<Message> = emptyList()
)
