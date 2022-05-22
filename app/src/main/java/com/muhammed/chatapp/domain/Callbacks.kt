package com.muhammed.chatapp.domain

import com.google.firebase.auth.FirebaseUser
import com.muhammed.chatapp.pojo.User

class Callbacks {
    interface AuthListener {
        fun onSuccess(fUser: FirebaseUser?, token: String? = null)
        fun onFailure(message: String)
    }
    interface AuthCompleteListener {
        fun onSuccess(user: User, token: String? = null)
        fun onFailure(message: String)
    }
    interface VerificationListener {
        fun onSuccess()
        fun onFailure(message: String)
    }
}