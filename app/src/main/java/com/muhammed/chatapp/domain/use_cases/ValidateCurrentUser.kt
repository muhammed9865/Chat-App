package com.muhammed.chatapp.domain.use_cases

import com.muhammed.chatapp.domain.OperationResult

class ValidateCurrentUser {
    fun execute(currentUserId: String, userId: String): OperationResult {
        if (currentUserId != userId) {
            return OperationResult(
                isSuccessful = false,
                errorMessage = "Not the current user"
            )
        }

        return OperationResult(isSuccessful = true)
    }
}