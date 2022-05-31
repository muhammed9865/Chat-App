package com.muhammed.chatapp.domain.use_cases

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class SerializeEntityUseCase @Inject constructor(
    val gson: Gson
) {
    fun <T> toString(entity: T): String {
        return gson.toJson(entity).toString()
    }

    inline fun <reified T> fromString(string: String): T {
        return gson.fromJson(string, T::class.java)
    }
}