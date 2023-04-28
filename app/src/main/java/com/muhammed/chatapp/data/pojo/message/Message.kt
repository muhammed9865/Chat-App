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
    val messageDate: Long = System.currentTimeMillis(),
    val imageUrl: String? = null,
    /*
      * in Private Chat if equals 1, delete message from remote database to save space
      * 1 because it will be read already by the firstUser so we don't have to increment it.
      * in Group Chat if equals to membersList size, remove from database to save space
     */
    var viewedTimes: Int = 0,
) {

    fun hasImage() = !imageUrl.isNullOrEmpty()

    companion object {
        fun emptyMessage() = Message()
    }
}
