package com.muhammed.chatapp.data.implementation.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.muhammed.chatapp.Constants
import com.muhammed.chatapp.data.pojo.user.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = Constants.USER_TOKEN)

class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {
    private val dataStore = appContext.dataStore

    suspend fun saveCurrentUserDetails(user: User?) {
        dataStore.edit {
            if (user != null) {
                val userAsString = Gson().toJson(user).toString()
                it[currentUserDetailsKey] = userAsString
                return@edit
            }

            it[currentUserDetailsKey] = ""
        }
    }

    val currentUserDetails = dataStore.data.map {
        it[currentUserDetailsKey]
    }.filterNotNull().map {
        Log.d("currentUserDetails", it )
        it.let { Gson().fromJson(it, User::class.java) }
    }


    private val currentUserDetailsKey = stringPreferencesKey("CurrentUserDetails")
}