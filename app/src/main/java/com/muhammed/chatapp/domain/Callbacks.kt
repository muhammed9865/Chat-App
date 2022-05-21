package com.muhammed.chatapp.domain

import com.google.firebase.auth.FirebaseUser
import com.muhammed.chatapp.pojo.User

class Callbacks {
    interface AuthListener {
        fun onSuccess(fUser: FirebaseUser?)
        fun onFailure(message: String)
    }
    interface AuthCompleteListener {
        fun onSuccess(user: User)
        fun onFailure(message: String)
    }
    interface VerificationListener {
        fun onSuccess()
        fun onFailure(message: String)
    }
}