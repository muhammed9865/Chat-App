package com.muhammed.chatapp.data.pojo

data class Message(
    val sender: User = User(),
    val text: String = "",
    val messageDate: Long = System.currentTimeMillis()
) {
    companion object {
        fun emptyMessage() = Message()
    }
}
