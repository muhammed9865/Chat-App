package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.CacheDatabase
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.pojo.message.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessagesRepository @Inject constructor(
    private val networkDatabase: NetworkDatabase,
    private val cacheDatabase: CacheDatabase
) {
    private val mMessages = mutableSetOf<Message>()

    suspend fun listenToMessages(
        messagesId: String,
        onNewMessages: (messages: List<Message>) -> Unit
    ) {
        mMessages.addAll(loadChatMessages(messagesId))
        networkDatabase.listenToChatMessages(messagesId) {
            val newMsgs = it.messages.toSet().subtract(mMessages)
            cacheDatabase.saveMessages(newMsgs.toList())
            onNewMessages(newMsgs.toList())

        }
    }

    suspend fun getRandomMessages(messagesId: String) = networkDatabase.getRandomMessages(messagesId)

    private suspend fun loadChatMessages(messagesId: String): List<Message> =
        cacheDatabase.loadMessages(messagesId)

    suspend fun sendMessage(chatId: String, messagesId: String, message: Message) =
        networkDatabase.sendMessage(chatId, messagesId, message)

}