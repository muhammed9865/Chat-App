package com.muhammed.chatapp.data.pojo

data class ChatStatus(
    val status: String = IDLE
) {
    companion object {
        const val TYPING = "typing.."
        const val IDLE = ""
    }
}


