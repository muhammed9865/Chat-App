package com.muhammed.chatapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.muhammed.chatapp.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = Constants.USER_TOKEN)
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {
    private val tokenDataStore = appContext.dataStore


    private val tokenKey = stringPreferencesKey(Constants.USER_TOKEN)
}