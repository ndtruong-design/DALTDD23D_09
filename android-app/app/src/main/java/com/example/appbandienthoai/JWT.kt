package com.example.appbandienthoai

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


val Context.dataStore by preferencesDataStore(name = "app_prefs")
val JWT_KEY = stringPreferencesKey("jwt_token")
suspend fun saveToken(context: Context, token: String) {
    context.dataStore.edit { it[JWT_KEY] = token }
}

suspend fun getToken(context: Context): String? {
    return context.dataStore.data.first()[JWT_KEY]
}
