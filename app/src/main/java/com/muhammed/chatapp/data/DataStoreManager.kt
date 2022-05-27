package com.muhammed.chatapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.muhammed.chatapp.Constants
import com.muhammed.chatapp.pojo.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = Constants.USER_TOKEN)
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {
    private val dataStore = appContext.dataStore

    suspend fun saveCurrentUserDetails(user: User) {
        dataStore.edit {
            val userAsString = Gson().toJson(user).toString()
            it[currentUserDetailsKey] = userAsString
        }
    }

    val currentUserDetails = dataStore.data.map {
        it[currentUserDetailsKey]
    }.map {
        it?.let {  Gson().fromJson(it, User::class.java) }
    }.cancellable()




   private val currentUserDetailsKey = stringPreferencesKey("CurrentUserDetails")
}