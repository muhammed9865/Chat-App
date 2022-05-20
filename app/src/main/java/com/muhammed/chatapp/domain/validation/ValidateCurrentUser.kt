package com.muhammed.chatapp.domain.validation

class ValidateCurrentUser {
    fun execute(currentUserId: String, userId: String): ValidationResult {
        if (currentUserId != userId) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "Not the current user"
            )
        }

        return ValidationResult(isSuccessful = true)
    }
}