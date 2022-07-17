package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.CacheDatabase
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.implementation.network.NotificationsManager
import com.muhammed.chatapp.data.pojo.chat.GroupChat
import com.muhammed.chatapp.data.pojo.message.Message
import javax.inject.Inject

class MessagesRepository @Inject constructor(
    private val networkDatabase: NetworkDatabase,
    private val cacheDatabase: CacheDatabase,
    private val notificationManager: NotificationsManager
) {
    private val mMessages = mutableSetOf<Message>()

    suspend fun listenToMessages(
        messagesId: String,
        onNewMessages: (messages: List<Message>) -> Unit
    ) {
        mMessages.addAll(loadChatMessages(messagesId))
        onNewMessages(mMessages.toList())
        networkDatabase.listenToChatMessages(messagesId) {
            val newMsgs = it.messages.toSet().subtract(mMessages)
            val msgsList = newMsgs.toList()
            cacheDatabase.saveMessages(msgsList)
            onNewMessages(msgsList)

        }
    }

    fun unregisterMessagesListener() = networkDatabase.cancelListeningToMessages()

    suspend fun getRandomMessages(messagesId: String) =
        networkDatabase.getRandomMessages(messagesId)

    private suspend fun loadChatMessages(messagesId: String): List<Message> =
        cacheDatabase.loadMessages(messagesId)

    suspend fun sendMessage(
        token: String,
        chatId: String,
        messagesId: String,
        message: Message,
        group: GroupChat? = null
    ) {
        networkDatabase.sendMessage(chatId, messagesId, message)
        val sendToId = if (group != null) {
            "/topics/${group.category}"
        } else token
        notificationManager.sendNotificationTo(sendToId, message)
    }


}