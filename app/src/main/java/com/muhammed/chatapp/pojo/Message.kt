package com.muhammed.chatapp.pojo

data class Message(
    val senderId: Long = 0,
    val text: String = "",
    val sendDate: Long = System.currentTimeMillis()
) {
    companion object {
        fun emptyMessage() = Message()
    }
}
