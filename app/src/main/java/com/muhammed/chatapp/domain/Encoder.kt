package com.muhammed.chatapp.domain

import android.util.Base64

object Encoder {
    fun encodePassword(password: String): String {
        return Base64.encode(password.toByteArray(), Base64.DEFAULT).joinToString()
    }

    fun decodePassword(password: String): String {
        return Base64.decode(password, Base64.DEFAULT).joinToString()
    }
}