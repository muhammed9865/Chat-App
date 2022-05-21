package com.muhammed.chatapp.domain.validation

data class OperationResult(
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)
