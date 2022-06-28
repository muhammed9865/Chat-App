package com.muhammed.chatapp.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.muhammed.chatapp.data.pojo.Message
import com.muhammed.chatapp.data.pojo.Messages
import com.muhammed.chatapp.data.pojo.PrivateChat
import com.muhammed.chatapp.data.pojo.User
import java.lang.NullPointerException

interface NetworkDatabase {
    suspend fun saveUser(user: User)

    suspend fun saveGoogleUser(user: User)

    @Throws(NullPointerException::class)
    suspend fun getUser(email: String): User

    suspend fun getGoogleUser(email: String): User?


    suspend fun createPrivateChat(otherUserEmail: String, currentUser: User): PrivateChat?

    suspend fun updateUserChatsList(userEmail: String, userCollection: String, chatId: String)

    fun listenToChatsChanges(listener: EventListener<QuerySnapshot>): ListenerRegistration

    fun listenToChatMessages(
        messagesId: String,
        onUpdate: suspend (messages: Messages) -> Unit
    )

    /*
    @param ChatId to set the last chat message to the @param message
    @param messagesId to add the message to the chat messages, should be a unique id to a unique messages document related to chatId
    */
    suspend fun sendMessage(chatId: String, messagesId: String, message: Message)
}