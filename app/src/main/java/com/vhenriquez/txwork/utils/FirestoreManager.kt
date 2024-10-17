package com.vhenriquez.txwork.utils

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.InstrumentEntity.Companion.TAG
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

sealed class FireRes<out T> {
    data object Loading : FireRes<Nothing>()
    data class Success<T>(val data: T): FireRes<T>()
    data class Error(val errorMessage: String): FireRes<Nothing>()
}

class FirestoreManager {
//    private val firestore = FirebaseFirestore.getInstance()
//
//    private val auth = FirebaseAuth.getInstance()
//    //var userId = auth.getCurrentUser()?.uid
//
//    suspend fun checkUserExists(firebaseUser: FirebaseUser) {
//        val user = hashMapOf<String, Any>(
//            UserEntity.USERNAME to firebaseUser.displayName!!,
//            UserEntity.EMAIL to firebaseUser.email!!,
//            UserEntity.PHOTO_URL to (firebaseUser.photoUrl?.toString() ?: ""))
//        val docSnapshot = firestore.collection("usersApp").document(firebaseUser.uid).get().await()
//
//        if (!docSnapshot.exists())
//            docSnapshot.reference.set(user).await()
//
//    }
//
//    suspend fun addNote(note: InstrumentEntity) {
//        note.id= "userId.toString()"
//        firestore.collection("notes").add(note).await()
//    }
//
//    @SuppressLint("SuspiciousIndentation")
//    suspend fun addActivity(activity: ActivityEntity) {
//        val activityEntity = mutableMapOf<String, Any>()
//        activityEntity[ActivityEntity.NAME] = activity.name
//        activityEntity[ActivityEntity.DATE] = activity.date
//        activityEntity[ActivityEntity.BUSINESS] = activity.business
//        activityEntity[ActivityEntity.BUSINESS_ID] = activity.businessId
//        activityEntity[ActivityEntity.WORK_ORDER] = activity.workOrder
//        activityEntity[ActivityEntity.SERVICE_ORDER] = activity.serviceOrder
//        val ref= firestore.collection("activities")
//        if (activity.id.isEmpty()){
//            activityEntity[ActivityEntity.USERS] = listOf(auth.currentUser?.uid ?: "")
//            activityEntity[ActivityEntity.AUTHOR] = auth.currentUser?.email ?: ""
//            activityEntity[ActivityEntity.STATUS] = "open"
//            ref.add(activityEntity).await()
//        }else{
//            ref.document(activity.id).update(activityEntity).await()
//        }
//    }
//
//    suspend fun addInstrument(instrument: InstrumentEntity) {
//        val instrumentEntity = mutableMapOf<String, Any>()
//        instrumentEntity[InstrumentEntity.BRAND] = instrument.brand
//        instrumentEntity[InstrumentEntity.MODEL] = instrument.model
//        instrumentEntity[InstrumentEntity.SERIAL] = instrument.serial
//        instrumentEntity[InstrumentEntity.DAMPING] = instrument.damping
//        instrumentEntity[InstrumentEntity.OUTPUT] = instrument.output
//        instrumentEntity[InstrumentEntity.SENSOR_TYPE] = instrument.sensorType
//        instrumentEntity[InstrumentEntity.MAGNITUDE] = instrument.magnitude
//        instrumentEntity[InstrumentEntity.MEASUREMENT_MIN] = instrument.measurementMin
//        instrumentEntity[InstrumentEntity.MEASUREMENT_MAX] = instrument.measurementMax
//        instrumentEntity[InstrumentEntity.MEASUREMENT_UNIT] = instrument.measurementUnit
//        instrumentEntity[InstrumentEntity.VERIFICATION_MIN] = instrument.verificationMin
//        instrumentEntity[InstrumentEntity.VERIFICATION_MAX] = instrument.verificationMax
//        instrumentEntity[InstrumentEntity.VERIFICATION_UNIT] = instrument.verificationUnit
//        instrumentEntity[InstrumentEntity.OBSERVATIONS] = instrument.observations
//        if (instrument.verificationMinSV.isNotEmpty()) {
//            instrumentEntity[InstrumentEntity.VERIFICATION_MIN_SV] = instrument.verificationMinSV
//            instrumentEntity[InstrumentEntity.VERIFICATION_MAX_SV] = instrument.verificationMaxSV
//            instrumentEntity[InstrumentEntity.VERIFICATION_UNIT_SV] = instrument.verificationUnitSV
//            instrumentEntity[InstrumentEntity.VERIFICATION_MIN_TV] = instrument.verificationMinTV
//            instrumentEntity[InstrumentEntity.VERIFICATION_MAX_TV] = instrument.verificationMaxTV
//            instrumentEntity[InstrumentEntity.VERIFICATION_UNIT_TV] = instrument.verificationUnitTV
//            instrumentEntity[InstrumentEntity.VERIFICATION_MIN_QV] = instrument.verificationMinQV
//            instrumentEntity[InstrumentEntity.VERIFICATION_MAX_QV] = instrument.verificationMaxQV
//            instrumentEntity[InstrumentEntity.VERIFICATION_UNIT_QV] = instrument.verificationUnitQV
//        }
//        val ref= firestore.collection("instruments")
//        if (instrument.id.isEmpty()){
//            ref.add(instrumentEntity).await()
//        }else{
//            ref.document(instrument.id).update(instrumentEntity).await()
//        }
//    }
//
//    suspend fun deleteNote(noteId: String) {
//        val noteRef = firestore.collection("notes").document(noteId)
//        noteRef.delete().await()
//    }
//
//    suspend fun deleteActivity(activityId: String) {
//        val activityRef = firestore.collection("activities").document(activityId)
//        activityRef.delete().await()
//    }
//
//    suspend fun deleteInstrument(instrumentId: String) = flow {
//        try {
//            val instrumentRef = firestore.collection("instruments").document(instrumentId)
//            instrumentRef.delete().await()
//            emit(FireRes.Success(true))
//        }
//        catch (e: Exception) {
//            emit(FireRes.Error(e.message.toString()))
//        }
//    }
//
//    fun getNotesFlow(): Flow<List<InstrumentEntity>> = callbackFlow {
//        val notesRef = firestore.collection("notes")
//            .whereEqualTo("userId", "userId").orderBy("title")
//
//        val subscription = notesRef.addSnapshotListener { snapshot, _ ->
//            snapshot?.let { querySnapshot ->
//                val notes = mutableListOf<InstrumentEntity>()
//                for (document in querySnapshot.documents) {
//                    val note = document.toObject(InstrumentEntity::class.java)
//                    note?.id = document.id
//                    note?.let { notes.add(it) }
//                }
//                trySend(notes).isSuccess
//            }
//        }
//        awaitClose { subscription.remove() }
//    }
//
//    fun getActivitiesFlow(): Flow<List<ActivityEntity>> = callbackFlow {
//        val activitiesRef = firestore.collection("activities")
//            .whereEqualTo(ActivityEntity.STATUS, "open")
//
//        val subscription = activitiesRef.addSnapshotListener { snapshot, _ ->
//            snapshot?.let { querySnapshot ->
//                val activities = mutableListOf<ActivityEntity>()
//                for (document in querySnapshot.documents) {
//                    val activity = document.toObject(ActivityEntity::class.java)
//                    activity?.id = document.id
//                    activity?.let { activities.add(it) }
//                }
//                trySend(activities).isSuccess
//            }
//        }
//        awaitClose { subscription.remove() }
//    }
//
//    fun getInstrumentsFlow(activityId: String?): Flow<List<InstrumentEntity>> = callbackFlow {
//        val instrumentsRef = if (!activityId.isNullOrEmpty()){
//            firestore.collection("instruments").whereArrayContains(InstrumentEntity.ACTIVITIES, activityId)
//        }else
//            firestore.collection("instruments")
//
//        val subscription = instrumentsRef.addSnapshotListener { snapshot, _ ->
//            snapshot?.let { querySnapshot ->
//                val instruments = mutableListOf<InstrumentEntity>()
//                for (document in querySnapshot.documents) {
//                    Log.d(TAG, document.toString())
//                    val instrument = document.toObject(InstrumentEntity::class.java)
//                    instrument?.id = document.id
//                    instrument?.let { instruments.add(it) }
//                }
//                trySend(instruments).isSuccess
//            }
//        }
//        awaitClose { subscription.remove() }
//    }
//
//    suspend fun getBusiness() = flow {
//       val querySnapshot = firestore.collection("companies").get().await()
//        val business = querySnapshot.documents.mapNotNull {
//            it.toObject(CompanyEntity::class.java)?.apply {
//                id = it.id
//            }
//        } as MutableList
//        business.add(CompanyEntity(name = "Otro"))
//
//        emit(business)
//    }
//
//    suspend fun toggleStatusActivity(activity: ActivityEntity) {
//        val activityRef = firestore.collection("activities").document(activity.id)
//        activityRef.update(ActivityEntity.STATUS, if (activity.status == "open") "closed" else "open").await()
//    }
//
//    suspend fun getActivitySelected(activityId: String?)  = flow {
//        try {
//            val activityEntity = firestore.collection("activities").document(activityId!!).get().await()
//                .toObject(ActivityEntity::class.java)?.apply {
//                    id = activityId
//                }
//            emit(FireRes.Success(activityEntity))
//        }catch (e: Exception) {
//            emit(FireRes.Error(e.message.toString()))
//        }
//
//    }
//
//    suspend fun deleteInstrumentToActivity(instrumentId: String, activityId: String?) = flow{
//        try {
//            firestore.collection("instruments").document(instrumentId).update(
//                InstrumentEntity.ACTIVITIES, FieldValue.arrayRemove(activityId))
//                .await()
//            emit(FireRes.Success(true))
//        }catch (e: Exception) {
//            emit(FireRes.Error(e.message.toString()))
//        }
//
//    }
//
//    suspend fun addInstrumentToActivity(instrument: InstrumentEntity, activityId: String?) {
//        firestore.collection("instruments").document(instrument.id).update(
//            InstrumentEntity.ACTIVITIES, FieldValue.arrayUnion(activityId))
//            .await()
//    }

}