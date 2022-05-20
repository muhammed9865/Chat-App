package com.muhammed.chatapp.domain.validation

class ValidatePassword {
    fun execute(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(isSuccessful = false, errorMessage = "Password field can't be empty")
        }
        if (password.length < 8) {
            return ValidationResult(isSuccessful = false, errorMessage = "Password length can't be less than 8 characters")
        }

        val containsLettersAndDigits = password.any { it.isDigit() } && password.any { it.isLetter() }
        if (!containsLettersAndDigits) {
            return ValidationResult(isSuccessful = false, errorMessage = "Password must contain at least a letter and a digit")
        }

        return ValidationResult(isSuccessful = true)
    }
}