package com.muhammed.chatapp.data

import com.muhammed.chatapp.data.pojo.message.Message

interface CacheDatabase {

    suspend fun saveMessages(messages: List<Message>)

    suspend fun loadMessages(chatId: String): List<Message>
}