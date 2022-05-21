package com.muhammed.chatapp.domain.validation

import android.util.Patterns

class ValidateEmail {
    fun execute(email: String): OperationResult {
        if (email.isBlank()) {
            return OperationResult(
                isSuccessful = false,
                errorMessage = "Email field can't be empty"
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return OperationResult(
                isSuccessful = false,
                errorMessage = "Email is invalid"
            )
        }

        return OperationResult(isSuccessful = true)
    }
}