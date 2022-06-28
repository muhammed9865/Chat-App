package com.muhammed.chatapp.data.implementation.local

import androidx.room.*
import com.muhammed.chatapp.data.pojo.Message
import com.muhammed.chatapp.domain.use_cases.SerializeEntityUseCase
import javax.inject.Inject

@Database(
    version = 2,
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
}