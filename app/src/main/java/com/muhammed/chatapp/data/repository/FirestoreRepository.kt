package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.FirestoreManager
import com.muhammed.chatapp.pojo.PrivateChat
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

    fun listenToUserChats(user: User, onChange: (room: PrivateChat) -> Unit) = fireStoreManager.listenToChatRooms(user.chats_list, onChange)
}