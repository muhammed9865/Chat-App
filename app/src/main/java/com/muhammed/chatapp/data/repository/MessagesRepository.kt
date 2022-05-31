package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.data.pojo.Message
import com.muhammed.chatapp.data.pojo.Messages
import javax.inject.Inject

class MessagesRepository @Inject constructor(
    private val firestoreManager: FirestoreManager
) {

    fun listenToMessages(messagesId: String, onNewMessages: (messages: Messages) -> Unit) =
        firestoreManager.listenToChatMessages(messagesId) { value, error ->
            if (error == null) {
                value?.toObject(Messages::class.java)?.let {
                    onNewMessages(it)
                }
            }
        }

    suspend fun sendMessage(chatId: String, messagesId: String, message: Message) =
        firestoreManager.sendMessage(chatId, messagesId, message)

}