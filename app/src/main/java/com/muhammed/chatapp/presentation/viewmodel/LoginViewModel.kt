package com.muhammed.chatapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.muhammed.chatapp.data.GoogleAuth
import com.muhammed.chatapp.domain.AuthRepository
import com.muhammed.chatapp.domain.validation.ValidateEmail
import com.muhammed.chatapp.domain.validation.ValidatePassword
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val googleAuth: GoogleAuth,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
): ViewModel() {

}