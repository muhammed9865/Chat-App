package com.muhammed.chatapp.data.repository

import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.pojo.user.User
import javax.inject.Inject

class InterestsAndTopicsRepository @Inject constructor(
    private val networkDatabase: NetworkDatabase
) {
    suspend fun getTopics() = networkDatabase.getTopics()
    suspend fun getInterests() = networkDatabase.getInterests()
    fun getUserInterestsWithTopics(user: User) = networkDatabase.getUserInterestsWithTopics(user)

}