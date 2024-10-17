package com.vhenriquez.txwork.model.repository

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.vhenriquez.txwork.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUserId: String
    val currentUserName: String
    val hasUser: Boolean

    val currentUser: Flow<UserEntity>
    suspend fun login(email: String, password: String): Flow<Resource<AuthResult>>
    suspend fun signup(name: String, email: String, password: String): Flow<Resource<AuthResult>>
    fun resetPassword(email: String)
    fun signout()
    //fun signInWithGoogle(googleSignInLauncher: ActivityResultLauncher<Intent>)

}