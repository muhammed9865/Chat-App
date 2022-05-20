package com.muhammed.chatapp.domain.validation

import android.util.Patterns

class ValidateEmail {
    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                isSuccessful = false,
                errorMessage = "Email field can't be empty"
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(isSuccessful = false, errorMessage = "Email is invalid")
        }

        return ValidationResult(isSuccessful = true)
    }
}