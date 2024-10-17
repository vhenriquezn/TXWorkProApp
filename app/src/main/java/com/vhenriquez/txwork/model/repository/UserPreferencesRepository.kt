package com.vhenriquez.txwork.model.repository

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val activitiesDataStore : Flow<Map<String,String>>
    val userFlow:Flow<UserEntity>
    val userId: Flow<String>
    suspend fun updateUser(firebaseUser: FirebaseUser)
    suspend fun updateRoles(roles: Map<String, Boolean>)
    val isUserLoggedInFlow: Flow<Boolean>
    suspend fun updateUserLoggedIn(isUserLoggedIn: Boolean)
    suspend fun updateCompanySelected(companySelected: CompanyEntity)
    val companyIdSelected: Flow<String>
    val companyNameSelected: Flow<String>
    suspend fun clearDatastore()
}