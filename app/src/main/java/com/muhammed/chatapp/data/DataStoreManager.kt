package com.muhammed.chatapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.muhammed.chatapp.Constants
import com.muhammed.chatapp.pojo.SavedUserDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = Constants.USER_TOKEN)
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {
    private val dataStore = appContext.dataStore

    suspend fun saveCurrentUserDetails(userEmail: String, userCategory: String, userName: String) {
        dataStore.edit {
            it[currentUserEmailKey] = userEmail
            it[currentUserCategoryKey] = userCategory
            it[currentUserNameKey] = userName
        }
    }



    val currentUserName = dataStore.data.map {
        it[currentUserNameKey]
    }

    val currentUserCategory = dataStore.data.map {
        it[currentUserCategoryKey]
    }

    val currentUserEmail = dataStore.data.map {
        it[currentUserEmailKey]
    }



    private val currentUserNameKey = stringPreferencesKey(Constants.USER_NAME)
    private val currentUserCategoryKey = stringPreferencesKey(Constants.USER_CATEGORY)
    private val currentUserEmailKey = stringPreferencesKey(Constants.USER_TOKEN)
}