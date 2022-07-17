package com.muhammed.chatapp.data.implementation.local

import androidx.room.*
import com.muhammed.chatapp.data.pojo.Interest
import com.muhammed.chatapp.data.pojo.Topic
import com.muhammed.chatapp.data.pojo.message.Message
import com.muhammed.chatapp.domain.use_cases.SerializeEntityUseCase
import javax.inject.Inject

@Database(
    version = 4,
    entities = [Message::class],
    exportSchema = true
)
@TypeConverters(ListConverter::class)
abstract class RoomDB : RoomDatabase() {
    abstract val messagesDao: CacheDatabaseImp
}

@ProvidedTypeConverter
class ListConverter @Inject constructor(private val serializer: SerializeEntityUseCase) {

    @TypeConverter
    fun listToString(list: List<String>): String {
        return serializer.toString(list)
    }

    @TypeConverter
    fun stringToList(string: String): List<String> {
        return serializer.fromString(string)
    }
    @TypeConverter
    fun interestToString(list: List<Interest>): String {
        return serializer.toString(list)
    }
    @TypeConverter
    fun stringToInterest(string: String): List<Interest> {
        return serializer.fromString(string)
    }

    @TypeConverter
    fun topicToString(list: List<Topic>): String {
        return serializer.toString(list)
    }
    @TypeConverter
    fun stringToTopic(string: String): List<Topic> {
        return serializer.fromString(string)
    }
}