package com.muhammed.chatapp.data.pojo.chat

import com.google.firebase.firestore.DocumentSnapshot
import com.muhammed.chatapp.data.pojo.message.Message

open class Chat(
    val cid: String = "",
    val messagesId: String = "",
    val createdSince: Long = System.currentTimeMillis(),
    val lastMessage: Message = Message(),
    val newMessagesCount: Int = 0
) {
    enum class TYPE {
        PRIVATE, GROUP
    }
    companion object {
        fun fromDocument(document: DocumentSnapshot): Chat? {
            if (document.data?.containsKey("firstUser") == true) {
                return document.toObject(PrivateChat::class.java)
            }
            if (document.data?.containsKey("category") == true) {
                return document.toObject(GroupChat::class.java)
            }
            return null
        }
    }
}
