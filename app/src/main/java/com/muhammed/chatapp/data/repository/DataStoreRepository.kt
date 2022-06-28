package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.implementation.local.DataStoreManager
import com.muhammed.chatapp.data.pojo.User
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    val currentUser = dataStoreManager.currentUserDetails

    suspend fun saveUserDetails(user: User) = dataStoreManager.saveCurrentUserDetails(user = user)

}