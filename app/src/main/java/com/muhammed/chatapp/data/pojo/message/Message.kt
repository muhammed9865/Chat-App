package com.muhammed.chatapp.data.pojo.message

import androidx.room.Embedded
import androidx.room.Entity
import com.muhammed.chatapp.data.pojo.user.User

@Entity(
    primaryKeys = ["messageDate","text"]
)
data class Message(
    val messagesId: String = "",
    @Embedded
    val sender: User = User(),
    val text: String = "",
    val messageDate: Long = System.currentTimeMillis()
) {

    override fun equals(other: Any?): Boolean {
        val otherMsg = other as Message
        return text == otherMsg.text && messageDate == otherMsg.messageDate
    }

    companion object {
        fun emptyMessage() = Message()
    }
}
