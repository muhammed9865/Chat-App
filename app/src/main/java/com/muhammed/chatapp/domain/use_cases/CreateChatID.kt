package com.muhammed.chatapp.domain.use_cases

import com.muhammed.chatapp.domain.OperationResult

class CreateChatID {
    fun execute(userEmail: String, otherUserEmail: String): String? {
        if (userEmail.isEmpty() || otherUserEmail.isEmpty()) {
            return null
        }

        return userEmail + System.currentTimeMillis().toString() + otherUserEmail
    }
}