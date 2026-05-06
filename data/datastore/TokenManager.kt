package com.example.shopping_site_andrio.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "shopping_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USER_ID = intPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val userId: Flow<Int?> = context.dataStore.data.map { it[KEY_USER_ID] }
    val username: Flow<String?> = context.dataStore.data.map { it[KEY_USERNAME] }
    val userRole: Flow<String?> = context.dataStore.data.map { it[KEY_USER_ROLE] }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }

    suspend fun saveAuthData(token: String, userId: Int, username: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
            prefs[KEY_USER_ROLE] = role
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USERNAME)
            prefs.remove(KEY_USER_ROLE)
            prefs[KEY_IS_LOGGED_IN] = false
        }
    }
}
