package com.muhammed.chatapp.domain.use_cases

import com.muhammed.chatapp.pojo.PrivateChat
import javax.inject.Inject

// Validates if the email is in user chats list
// Returns the user email if exists or null if doesn't


class ValidateUserExists @Inject constructor(){
    fun execute(email: String, chats: List<PrivateChat>): PrivateChat? {
        var exists: PrivateChat? = null
        chats.forEach { privateChat ->
            if (privateChat.firstUser.email == email) exists = privateChat
            if (privateChat.secondUser.email == email) exists = privateChat
        }

        return exists
    }
}