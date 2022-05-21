package com.muhammed.chatapp.pojo

import com.muhammed.chatapp.Constants

data class User(
    val uid: String,
    var nickname: String,
    val email: String,
    var profile_picture: String = Constants.MALE_PATH
)
