package com.muhammed.chatapp.data.implementation.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muhammed.chatapp.data.CacheDatabase
import com.muhammed.chatapp.data.pojo.Message

@Dao
abstract class CacheDatabaseImp : CacheDatabase {
    override suspend fun saveMessages(messages: List<Message>) {
        _saveMessages(messages)
    }

    override suspend fun loadMessages(chatId: String): List<Message> {
        return _loadMessages(chatId)
    }

    @Query("SELECT * FROM Message WHERE messagesId = :messagesId")
    protected abstract suspend fun _loadMessages(messagesId: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun _saveMessages(messages: List<Message>)
}