package com.muhammed.chatapp.domain.use_cases

import com.muhammed.chatapp.data.pojo.PrivateChat
import javax.inject.Inject

// Validates if the email is in user chats list
// Returns the user email if exists or null if doesn't


class ValidateUserIsAlreadyFriend @Inject constructor(

){
    fun execute(email: String, currentUserChats: List<PrivateChat>): PrivateChat? {
        var exists: PrivateChat? = null
        currentUserChats.forEach {
            if (it.firstUser.email == email || it.secondUser.email == email) {
                exists = it
            }
        }

        return exists
    }
}