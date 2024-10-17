package com.vhenriquez.txwork.model.repository.impl

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.await
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.model.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore : FirebaseFirestore,
) : AuthRepository {
    override val currentUserId: String
        get() = firebaseAuth.currentUser?.uid.orEmpty()

    override val currentUserName: String
        get() = firebaseAuth.currentUser?.displayName.orEmpty()

    override val hasUser: Boolean
        get() = firebaseAuth.currentUser != null

    override val currentUser: Flow<UserEntity>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let   {
                            UserEntity(id = it.uid,
                                userName = it.displayName?:"",
                                email = it.email,
                                photoUrl = it.photoUrl?.toString()) } ?: UserEntity())
                }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }

    override suspend fun login(email: String, password: String) : Flow<Resource<AuthResult>> {
        return flow {
            emit(value = Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                addUserToFirebase(it)
            }
            emit(value = Resource.Success(data = result))
        }.catch {
            emit(value = Resource.Error(it.message.toString()))
        }

    }

    override suspend fun signup(name: String, email: String, password: String) : Flow<Resource<AuthResult>> {
        return flow {
            emit(value = Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                addUserToFirebase(it)
            }
            emit(value = Resource.Success(data = result))
        }.catch {
            emit(value = Resource.Error(it.message.toString()))
        }

    }

    override fun resetPassword(email: String)  {
        firebaseAuth.sendPasswordResetEmail(email)
    }

    override fun signout() {
        firebaseAuth.signOut()
    }

    private suspend fun addUserToFirebase(firebaseUser: FirebaseUser){
        firebaseUser.apply {
            val user = toUser()
            val docSnapshot = firestore.collection("usersApp").document(uid).get().await()
                if(!docSnapshot.exists()){
                    docSnapshot.reference.set(user).await()
                }
        }
    }

    private fun FirebaseUser.toUser() = mapOf(
        UserEntity.USERNAME to displayName,
        UserEntity.EMAIL to email,
        UserEntity.PHOTO_URL to photoUrl?.toString(),
    )
}