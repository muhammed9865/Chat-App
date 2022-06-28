package com.muhammed.chatapp.data.pojo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

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


    companion object {
        fun emptyMessage() = Message()
    }
}
