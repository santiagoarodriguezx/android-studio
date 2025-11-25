package com.example.myapplication.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private const val TAG = "TokenManager"
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val DEVICE_FINGERPRINT_KEY = stringPreferencesKey("device_fingerprint")
        private val COMPANY_ID_KEY = stringPreferencesKey("company_id")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String, companyId: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            companyId?.let { preferences[COMPANY_ID_KEY] = it }
        }
        Log.d(TAG, "✅ Tokens guardados. AccessToken length: ${accessToken.length}, RefreshToken length: ${refreshToken.length}")
        Log.d(TAG, "AccessToken preview: ${accessToken.take(50)}...")
        companyId?.let { Log.d(TAG, "🏢 Company ID guardado: $it") }
    }

    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
        Log.d(TAG, "✅ Email guardado: $email")
    }

    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            val token = preferences[ACCESS_TOKEN_KEY]
            Log.d(TAG, "📤 getAccessToken: ${if (token != null) "Token existe (${token.length} chars)" else "null"}")
            token
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    fun getUserEmail(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(COMPANY_ID_KEY)
        }
        Log.d(TAG, "🗑️ Tokens eliminados")
    }

    // ==================== DEVICE FINGERPRINT ====================

    suspend fun saveDeviceFingerprint(fingerprint: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_FINGERPRINT_KEY] = fingerprint
        }
        Log.d(TAG, "📱 Device fingerprint guardado: $fingerprint")
    }

    fun getDeviceFingerprint(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[DEVICE_FINGERPRINT_KEY]
        }
    }

    suspend fun clearDeviceFingerprint() {
        context.dataStore.edit { preferences ->
            preferences.remove(DEVICE_FINGERPRINT_KEY)
        }
        Log.d(TAG, "🗑️ Device fingerprint eliminado")
    }

    // ==================== COMPANY ID ====================

    fun getCompanyId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[COMPANY_ID_KEY]
        }
    }
}

