package com.muhammed.chatapp.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.pojo.User
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val fireStoreManager: FirestoreManager
) {

    suspend fun createNewPrivateChat(otherUserEmail: String, currentUser: User) =
        fireStoreManager.createPrivateChatRoom(otherUserEmail, currentUser = currentUser)

    suspend fun updateUserChatsList(email: String, userCollection: String, chatId: String) =
        fireStoreManager.updateUserChatsList(email, userCollection, chatId)

    suspend fun getUser(userEmail: String) = fireStoreManager.getUser(userEmail)

    suspend fun getUserChats(user: User) = fireStoreManager.getUserChats(user)

    fun listenToUserChats(user: User, listener: EventListener<DocumentSnapshot>) =
        fireStoreManager.listenToUserChats(user, listener)

    fun listenToChatsChanges(chat_ids: List<String>, listener: EventListener<QuerySnapshot>) =
        fireStoreManager.listenToChatsChanges(chat_ids, listener = listener)

    suspend fun loadChats(chat_ids: List<String>) = fireStoreManager.loadChats(chat_ids)
}