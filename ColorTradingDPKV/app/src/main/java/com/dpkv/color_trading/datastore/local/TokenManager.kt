package com.dpkv.color_trading.datastore.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name="auth_prefs")

class TokenManager (private val context: Context) {
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    suspend fun saveTokens(
        accessToken : String,
        refreshToken : String
    ){
        context.dataStore.edit {
            it[ACCESS_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveAccessToken(
        accessToken: String
    ) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = accessToken
        }
    }

    val accessToken : Flow<String?> = context.dataStore.data.map {pref->
        pref[ACCESS_TOKEN]

    }
    val refreshToken: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[REFRESH_TOKEN] }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data
            .map { it[ACCESS_TOKEN] }
            .first()
    }

    suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }

    suspend fun logout() {
        clearTokens()
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data
            .map { it[REFRESH_TOKEN] }
            .first()
    }
    suspend fun clearTokens() {
        context.dataStore.edit {
            it.clear()
        }
    }
}

