package com.muhammed.chatapp.data.pojo

import com.google.firebase.auth.FirebaseUser

data class AuthResult(
    val user: FirebaseUser? = null,
    val isSuccessful: Boolean,
    val errorMessage: String? = null

)
