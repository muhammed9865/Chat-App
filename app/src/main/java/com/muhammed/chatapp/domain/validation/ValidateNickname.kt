package com.muhammed.chatapp.domain.validation

class ValidateNickname {
    fun execute(name: String): OperationResult {
        if (name.isBlank()) {
            return OperationResult(isSuccessful = false, errorMessage = "Name field can't be empty")
        }
        return OperationResult(isSuccessful = true)
    }
}