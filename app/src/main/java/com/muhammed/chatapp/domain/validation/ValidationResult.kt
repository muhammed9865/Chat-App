package com.muhammed.chatapp.domain.validation

data class ValidationResult(
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)
