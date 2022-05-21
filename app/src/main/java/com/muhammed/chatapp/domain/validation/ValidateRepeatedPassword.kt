package com.muhammed.chatapp.domain.validation

import dagger.Provides

class ValidateRepeatedPassword {
    fun execute(password: String, repeatedPassword: String): OperationResult {
        if (repeatedPassword != password) {
            return OperationResult(
                isSuccessful = false,
                errorMessage = "Repeated password must match the password"
            )
        }

        if (repeatedPassword.isBlank()) {
            return OperationResult(
                isSuccessful = false,
                errorMessage = "This field can't be empty"
            )
        }

        return OperationResult(isSuccessful = true)
    }
}