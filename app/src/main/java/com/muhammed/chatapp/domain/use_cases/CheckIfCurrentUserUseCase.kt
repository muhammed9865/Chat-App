package com.muhammed.chatapp.domain.use_cases

import com.muhammed.chatapp.domain.OperationResult
import javax.inject.Inject


class CheckIfCurrentUserUseCase @Inject constructor() {
    fun execute(currentUserEmail: String, otherEmail: String): OperationResult {
        if (currentUserEmail != otherEmail) {
            return OperationResult(
                isSuccessful = false,
                errorMessage = "Not the current user"
            )
        }

        return OperationResult(isSuccessful = true)
    }
}