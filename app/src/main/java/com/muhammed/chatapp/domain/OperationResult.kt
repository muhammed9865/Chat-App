package com.muhammed.chatapp.domain

data class OperationResult(
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)
