package com.muhammed.chatapp.data.pojo.chat

import com.muhammed.chatapp.data.pojo.user.User

data class NewGroupChat(
    val title: String,
    val description: String,
    val category: String,
    val photo: String,
    val currentUser: User
)