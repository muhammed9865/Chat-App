package com.muhammed.chatapp.data.pojo

import com.muhammed.chatapp.Constants

data class User(
    val uid: String = "",
    var nickname: String = "",
    val email: String = "",
    val password: String? = "",
    var profile_picture: String = Constants.MALE_PATH,
    val collection: String = "",
    var chats_list: List<String> = emptyList(),
    var isFirstLogin: Boolean = true,
    var interests: List<Interest> = emptyList(),
    var topics: List<Topic> = emptyList()
)
