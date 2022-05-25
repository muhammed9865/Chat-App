package com.muhammed.chatapp.domain.use_cases

import android.util.Patterns
import com.muhammed.chatapp.domain.OperationResult

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