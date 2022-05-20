package com.muhammed.chatapp.domain.validation

class ValidateRepeatedPassword {
    fun execute(password: String, repeatedPassword: String): ValidationResult {
        if (repeatedPassword != password) {
            return ValidationResult(isSuccessful = false, errorMessage = "Repeated password must match the password")
        }

        if (repeatedPassword.isBlank()) {
            return ValidationResult(isSuccessful = false, errorMessage = "This field can't be empty")
        }

        return ValidationResult(isSuccessful = true)
    }
}