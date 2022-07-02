package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.implementation.local.DataStoreManager
import com.muhammed.chatapp.data.pojo.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val networkDatabase: NetworkDatabase,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun updateUser(user: User) {
        saveUserDetails(user)
        networkDatabase.updateUser(user)
    }

    suspend fun updateDummyUser(user: User) {
        saveUserDetails(user)
    }

    suspend fun getUser(userEmail: String) = networkDatabase.getUser(userEmail)

    suspend fun updateUserChatsList(email: String, userCollection: String, chatId: String) =
        networkDatabase.updateUserChatsList(email, userCollection, chatId)

    val currentUser = dataStoreManager.currentUserDetails

    suspend fun saveUserDetails(user: User) = dataStoreManager.saveCurrentUserDetails(user = user)


}