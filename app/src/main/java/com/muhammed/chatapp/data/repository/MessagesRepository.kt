package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.CacheDatabase
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.pojo.Message
import com.muhammed.chatapp.data.pojo.Messages
import javax.inject.Inject

class MessagesRepository @Inject constructor(
    private val networkDatabase: NetworkDatabase,
    private val cacheDatabase: CacheDatabase
) {

    fun listenToMessages(messagesId: String, onNewMessages: (messages: List<Message>) -> Unit) =
        networkDatabase.listenToChatMessages(messagesId) {
            cacheDatabase.saveMessages(it.messages)
            val messages = loadChatMessages(messagesId)
            onNewMessages(messages)
        }

    private suspend fun loadChatMessages(messagesId: String): List<Message> = cacheDatabase.loadMessages(messagesId)

    suspend fun sendMessage(chatId: String, messagesId: String, message: Message) =
        networkDatabase.sendMessage(chatId, messagesId, message)

}