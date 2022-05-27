package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.FirestoreManager
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firestoreManager: FirestoreManager
) {

    suspend fun createNewPrivateChat(otherUserEmail: String) =
        firestoreManager.createPrivateChatRoom(otherUserEmail)

    suspend fun updateUserChatsList(email: String, userCollection: String, chatId: String) =
        firestoreManager.updateUserChatsList(email, userCollection, chatId)

    suspend fun getUser(userEmail: String) = firestoreManager.getUser(userEmail)

    suspend fun getUserChats(email: String, userCollection: String) = firestoreManager.getUserChats(email, userCollection)
}