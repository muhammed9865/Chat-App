package com.muhammed.chatapp.domain.validation

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