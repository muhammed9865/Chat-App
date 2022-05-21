package com.muhammed.chatapp.presentation.state

data class ValidationState(
    val nickname: String = "",
    val nicknameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val repeatedPassword: String = "",
    val repeatedPasswordError: String? = null
)
