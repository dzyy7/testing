package id.co.psplauncher.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "psp_mobile_data_store")

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val appContext = context.applicationContext

    val accessToken: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN] ?: ""
        }

    val userName: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[USER_NAME] ?: ""
        }

    val merchantName: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[MERCHANT_NAME] ?: ""
        }

    val roleType: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ROLE_TYPE] ?: ""
        }

    suspend fun saveAccessToken(accessToken: String) {
        appContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun saveUserName(name: String) {
        appContext.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun saveMerchantName(name: String) {
        appContext.dataStore.edit { preferences ->
            preferences[MERCHANT_NAME] = name
        }
    }

    suspend fun saveRoleType(role: String) {
        appContext.dataStore.edit { preferences ->
            preferences[ROLE_TYPE] = role
        }
    }

    suspend fun clearToken() {
        appContext.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(USER_NAME)
            preferences.remove(MERCHANT_NAME)
            preferences.remove(ROLE_TYPE)
        }
    }

    fun getAccessToken() = runBlocking(Dispatchers.IO) {
        accessToken.first()
    }

    fun getUserName() = runBlocking(Dispatchers.IO) {
        userName.first()
    }

    fun getMerchantName() = runBlocking(Dispatchers.IO) {
        merchantName.first()
    }

    fun getRoleType() = runBlocking(Dispatchers.IO) {
        roleType.first()
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val MERCHANT_NAME = stringPreferencesKey("merchant_name")
        private val ROLE_TYPE = stringPreferencesKey("role_type")
    }
}
