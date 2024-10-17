package com.vhenriquez.txwork

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseUser
import com.vhenriquez.txwork.model.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "txWorPreferences")

@Singleton
class MyPreferencesDataStore @Inject constructor(
    @ApplicationContext context: Context
){
    private val dataStore: DataStore<Preferences> = context.dataStore

    private object PreferencesKey{
        val KEY_USER_LOGGED_IN = booleanPreferencesKey(name = "isUserLoggedIn")
        val KEY_NAME = stringPreferencesKey(name = "name")
        val KEY_PHOTO_URL = stringPreferencesKey(name = "photoUrl")
        val KEY_EMAIL = stringPreferencesKey(name = "email")
        val KEY_USER_UID = stringPreferencesKey(name = "userId")
        val KEY_COMPANY_ID_SELECTED = stringPreferencesKey(name = "companyIdSelected")
        val KEY_COMPANY_NAME_SELECTED = stringPreferencesKey(name = "companyNameSelected")
        val KEY_ROL_INSTRUMENTS = booleanPreferencesKey(name = "showInstruments")
        val KEY_ROL_USERS = booleanPreferencesKey(name = "showUsers")
        val KEY_ROL_BUSINESS = booleanPreferencesKey(name = "showBusiness")
        val KEY_ROL_PATTERNS = booleanPreferencesKey(name = "showPatterns")
        val KEY_ROL_TOOLS = booleanPreferencesKey(name = "showTools")
    }

    val userFlow = dataStore.data
        .catch { exeption->
            if (exeption is IOException){
                emit(emptyPreferences())
            }
            else{
                throw exeption
            }
        }.map { preferences->
        UserEntity(
            id = preferences[PreferencesKey.KEY_USER_UID] ?: "",
            userName = preferences[PreferencesKey.KEY_NAME] ?: "",
            email = preferences[PreferencesKey.KEY_EMAIL] ?: "",
            photoUrl = preferences[PreferencesKey.KEY_PHOTO_URL],
            roles = mapOf(
                "showInstruments" to (preferences[PreferencesKey.KEY_ROL_INSTRUMENTS] == true),
                "showUsers" to (preferences[PreferencesKey.KEY_ROL_USERS] == true),
                "showBusiness" to (preferences[PreferencesKey.KEY_ROL_BUSINESS] == true),
                "showPatterns" to (preferences[PreferencesKey.KEY_ROL_PATTERNS] == true),
                "showTools" to (preferences[PreferencesKey.KEY_ROL_TOOLS] == true),
            )
        )
    }

    suspend fun updateUser(firebaseUser: FirebaseUser){
        dataStore.edit {preferences->
            preferences[PreferencesKey.KEY_USER_LOGGED_IN] = true
            preferences[PreferencesKey.KEY_NAME] = firebaseUser.displayName ?: ""
            firebaseUser.photoUrl?.toString()?.let { preferences[PreferencesKey.KEY_PHOTO_URL] = it }
            preferences[PreferencesKey.KEY_EMAIL] = firebaseUser.email ?: ""
            preferences[PreferencesKey.KEY_USER_UID] = firebaseUser.uid
        }
    }

    suspend fun updateRoles(roles: Map<String, Boolean>) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.KEY_ROL_INSTRUMENTS] = roles["showInstruments"] ?: false
            preferences[PreferencesKey.KEY_ROL_USERS] = roles["showUsers"] ?: false
            preferences[PreferencesKey.KEY_ROL_BUSINESS] = roles["showBusiness"] ?: false
            preferences[PreferencesKey.KEY_ROL_PATTERNS] = roles["showPatterns"] ?: false
            preferences[PreferencesKey.KEY_ROL_TOOLS] = roles["showTools"] ?: false
        }
    }
}