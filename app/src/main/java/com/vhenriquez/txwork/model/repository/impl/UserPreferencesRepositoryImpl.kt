package com.vhenriquez.txwork.model.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseUser
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.model.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val userPreferences: DataStore<Preferences>
) : UserPreferencesRepository {

    override val activitiesDataStore = userPreferences.data
        .catch { exeption->
        if (exeption is IOException){
            emit(emptyPreferences())
        }
        else{
            throw exeption
        }
    }.map { preferences->
        mapOf(
            "filter" to (preferences[PreferencesKey.KEY_ACTIVITIES_FILTER] ?: "open"),
            "companyId" to (preferences[PreferencesKey.KEY_COMPANY_ID_SELECTED]?:""),
            "userId" to (preferences[PreferencesKey.KEY_USER_UID]?:""))
        }

    override val userFlow = userPreferences.data
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
                    "instruments" to (preferences[PreferencesKey.KEY_ROL_INSTRUMENTS] == true),
                    "users" to (preferences[PreferencesKey.KEY_ROL_USERS] == true),
                    "business" to (preferences[PreferencesKey.KEY_ROL_BUSINESS] == true),
                    "patterns" to (preferences[PreferencesKey.KEY_ROL_PATTERNS] == true),
                    "tools" to (preferences[PreferencesKey.KEY_ROL_TOOLS] == true),
                )
            )
        }

    override val userId = userPreferences.data
        .catch { exeption->
            if (exeption is IOException){
                emit(emptyPreferences())
            }
            else{
                throw exeption
            }
        }.map { preferences->
            preferences[PreferencesKey.KEY_USER_UID]?: ""
        }

    override suspend fun updateUser(firebaseUser: FirebaseUser){
        userPreferences.edit {preferences->
            preferences[PreferencesKey.KEY_USER_LOGGED_IN] = true
            preferences[PreferencesKey.KEY_NAME] = firebaseUser.displayName ?: ""
            firebaseUser.photoUrl?.toString()?.let { preferences[PreferencesKey.KEY_PHOTO_URL] = it }
            preferences[PreferencesKey.KEY_EMAIL] = firebaseUser.email ?: ""
            preferences[PreferencesKey.KEY_USER_UID] = firebaseUser.uid
        }
    }

    override suspend fun updateRoles(roles: Map<String, Boolean>) {
        userPreferences.edit { preferences ->
            preferences[PreferencesKey.KEY_ROL_INSTRUMENTS] = roles["instruments"] == true
            preferences[PreferencesKey.KEY_ROL_USERS] = roles["users"] == true
            preferences[PreferencesKey.KEY_ROL_BUSINESS] = roles["business"] == true
            preferences[PreferencesKey.KEY_ROL_PATTERNS] = roles["patterns"] == true
            preferences[PreferencesKey.KEY_ROL_TOOLS] = roles["tools"] == true
        }
    }

    override val isUserLoggedInFlow = userPreferences.data
        .catch { exeption->
            if (exeption is IOException){
                emit(emptyPreferences())
            }
            else{
                throw exeption
            }
        }.map { preferences->
            preferences[PreferencesKey.KEY_USER_LOGGED_IN]?:false
        }

    override suspend fun updateUserLoggedIn(isUserLoggedIn: Boolean){
        userPreferences.edit { preferences ->
            preferences[PreferencesKey.KEY_USER_LOGGED_IN] = isUserLoggedIn
        }
    }

    override suspend fun updateCompanySelected(companySelected: CompanyEntity) {
        userPreferences.edit { preferences ->
            preferences[PreferencesKey.KEY_COMPANY_ID_SELECTED] = companySelected.id
            preferences[PreferencesKey.KEY_COMPANY_NAME_SELECTED] = companySelected.name
        }
    }

    override val companyIdSelected = userPreferences.data
        .catch { exeption->
            if (exeption is IOException){
                emit(emptyPreferences())
            }
            else{
                throw exeption
            }
        }.map { preferences->
            preferences[PreferencesKey.KEY_COMPANY_ID_SELECTED]?: ""
        }

    override val companyNameSelected = userPreferences.data
        .catch { exeption->
            if (exeption is IOException){
                emit(emptyPreferences())
            }
            else{
                throw exeption
            }
        }.map { preferences->
            preferences[PreferencesKey.KEY_COMPANY_NAME_SELECTED]?: ""
        }

    override suspend fun clearDatastore() {
        userPreferences.edit {
            it.clear()
        }
    }

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
    val KEY_ACTIVITIES_FILTER = stringPreferencesKey(name = "filterActivities")
}
}