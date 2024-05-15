package com.domagojleskovic.bleadvert

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class UserInfoStorage(private val context: Context) {
    companion object {
        private const val DATASTORE_NAME = "user_info"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val IS_LOGGED_IN = booleanPreferencesKey("logged_in")
    }
    val getLoggedIn = context.dataStore.data.map {
        it[IS_LOGGED_IN] ?: false
    }
    val getEmail = context.dataStore.data.map {
        it[EMAIL] ?: ""
    }
    val getPassword = context.dataStore.data.map {
        it[PASSWORD] ?: ""
    }
    suspend fun setLoggedIn(boolean: Boolean){
        context.dataStore.edit {
            it[IS_LOGGED_IN] = boolean
        }
    }
    suspend fun setEmail(email: String){
        context.dataStore.edit {
            it[EMAIL] = email
        }
    }
    suspend fun setPassword(password: String){
        context.dataStore.edit {
            it[PASSWORD] = password
        }
    }
    suspend fun setEmailAndPassword(email: String, password: String){
        context.dataStore.edit {
            it[PASSWORD] = password
            it[EMAIL] = email
        }
    }
}