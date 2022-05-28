package com.muhammed.chatapp.pojo

data class ChatStatus(
    private val status: String
) {
    companion object {
        const val TYPING = "typing.."
        const val IDLE = ""
    }
}


