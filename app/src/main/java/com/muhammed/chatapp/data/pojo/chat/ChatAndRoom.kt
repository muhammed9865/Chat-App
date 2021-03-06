package com.muhammed.chatapp.data.pojo.chat

// Use this class when entering the MessagingRoomActivity as Intent Extra instead of MessagingRoom
data class ChatAndRoom <Chat>(
    val chat: Chat,
    val room: MessagingRoom
)