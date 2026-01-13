package com.example.appbandienthoai

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

val Context.dataStore by preferencesDataStore(name = "app_prefs")
val USER_ID_KEY = intPreferencesKey("MaKhachHang")
val JWT_KEY = stringPreferencesKey("jwt_token")
val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
val USERNAME_KEY = stringPreferencesKey("username")
val PASSWORD_KEY = stringPreferencesKey("password")
suspend fun saveToken(context: Context, token: String) {
    context.dataStore.edit {
        it[JWT_KEY] = token
    }
}

suspend fun getToken(context: Context): String? {
    return context.dataStore.data.first()[JWT_KEY]
}


suspend fun savePassword(context: Context, password: String) {
    context.dataStore.edit {
        it[PASSWORD_KEY] = password
    }
}



suspend fun saveUserId(context: Context, userId: Int) {
    context.dataStore.edit { preferences ->
        preferences[USER_ID_KEY] = userId
    }
}

suspend fun getUserId(context: Context): Int {
    return context.dataStore.data.first()[USER_ID_KEY] ?: -1
}
suspend fun getPassword(context: Context): String? {
    return context.dataStore.data.first()[PASSWORD_KEY]
}

suspend fun saveUsername(context: Context, username: String) {
    context.dataStore.edit {
        it[USERNAME_KEY] = username
    }
}

suspend fun getUsername(context: Context): String? {
    return context.dataStore.data.first()[USERNAME_KEY]
}

suspend fun saveRememberMe(context: Context, remember: Boolean) {
    context.dataStore.edit {
        it[REMEMBER_ME_KEY] = remember
    }
}

suspend fun getRememberMe(context: Context): Boolean {
    return context.dataStore.data.first()[REMEMBER_ME_KEY] ?: false
}



