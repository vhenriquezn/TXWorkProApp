package com.vhenriquez.txwork.model.repository.impl

import android.util.Log
import androidx.compose.ui.res.stringResource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.await
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.ImageEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.PatternEntity
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.utils.Constants.Companion.ACTIVITIES_COLLECTION
import com.vhenriquez.txwork.utils.Constants.Companion.CERTIFICATES_COLLECTION
import com.vhenriquez.txwork.utils.Constants.Companion.COMPANIES_APP_COLLECTION
import com.vhenriquez.txwork.utils.Constants.Companion.COMPANY_COLLECTION
import com.vhenriquez.txwork.utils.Constants.Companion.IMAGES_COLLECTION
import com.vhenriquez.txwork.utils.Constants.Companion.INSTRUMENTS_COLLECTION
import com.vhenriquez.txwork.utils.Constants.Companion.PATTERNS_COLLECTION
import com.vhenriquez.txwork.utils.Constants.Companion.USERS_COLLECTION
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val firestore : FirebaseFirestore
) : FirestoreRepository {

    private val companiesAppCollection
        get() = firestore.collection(COMPANIES_APP_COLLECTION)

    private val activitiesCollection
        get() = firestore.collection(ACTIVITIES_COLLECTION)

    private val usersCollection
        get() = firestore.collection(USERS_COLLECTION)

    private val companiesCollection
        get() = firestore.collection(COMPANY_COLLECTION)

    private val patternsCollection
        get() = firestore.collection(PATTERNS_COLLECTION)

    private val certificatesCollection
        get() = firestore.collection(CERTIFICATES_COLLECTION)

    private val instrumentsCollection
        get() = firestore.collection(INSTRUMENTS_COLLECTION)

    override suspend fun getCompanyAppSelected(companyId: String): CompanyEntity? =
        companiesAppCollection.document(companyId).get().await()
            .toObject(CompanyEntity::class.java)?.apply {
                id = companyId
            }

    override suspend fun getUserInCompanyApp(companyId: String, userId: String): Map<String, Any> =
        companiesAppCollection.document(companyId).collection("users").document(userId).get().await()
            .data?: emptyMap()

    override suspend fun saveCompanyApp(company: Map<String, Any>){
        companiesAppCollection.add(company).await()
    }

    override suspend fun updateCompanyApp(company: Map<String, Any>, companyId: String): Resource<Boolean> {
        return try {
            companiesAppCollection.document(companyId).update(company).await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun deleteCompanyApp(companyId: String): Resource<Boolean> {
        return try {
            companiesAppCollection.document(companyId).delete().await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getCompaniesAppFlow(userId: String): Flow<Resource<List<CompanyEntity>>> = callbackFlow {
        if (userId.isEmpty()) {
            awaitCancellation()
        }
        val companiesRef = companiesAppCollection.where(
            Filter.or(
                Filter.equalTo(CompanyEntity.OWNER_ID, userId),
                Filter.arrayContains(CompanyEntity.USERS, userId)
            )
        )
        val subscription = companiesRef.addSnapshotListener { snapshot, exception ->
            if (exception != null){
                trySend(Resource.Error(exception.message.toString()))
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull {documentSnapshot ->
                documentSnapshot.toObject(CompanyEntity::class.java)?.apply {
                    id = documentSnapshot.id
                }
            } ?: emptyList()
            trySend(Resource.Success(items))
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun getActivitiesFlow(companyId: String, filter: String?, userId: String?): Flow<Resource<List<ActivityEntity>>> = callbackFlow{
        if (companyId.isEmpty()) {
            awaitCancellation()
        }
        val activitiesRef = activitiesCollection.where(
                Filter.and(
                    Filter.equalTo(ActivityEntity.STATUS, filter),
                    Filter.or(
                        Filter.equalTo(ActivityEntity.OWNER_ID, userId),
                        Filter.arrayContains(ActivityEntity.USERS, userId)
                    ),
                    Filter.equalTo(ActivityEntity.COMPANY_APP_ID, companyId)
                )
            )
            val subscription = activitiesRef.addSnapshotListener { snapshot, exception ->
                if (exception != null){
                    trySend(Resource.Error(exception.message.toString()))
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull {documentSnapshot ->
                    documentSnapshot.toObject(ActivityEntity::class.java)?.apply {
                        id = documentSnapshot.id
                    }
                } ?: emptyList()
                trySend(Resource.Success(items))
            }
            awaitClose { subscription.remove() }
        }


    override suspend fun saveActivity(activity: Map<String, Any>) {
        activitiesCollection.add(activity).await()
    }

    override suspend fun updateActivity(activity: Map<String, Any>, activityId: String){
        activitiesCollection.document(activityId).update(activity)
    }

    override suspend fun deleteActivity(activityId: String): Resource<Boolean> {
        return try {
            activitiesCollection.document(activityId).delete().await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getActivitySelected(activityId: String?): ActivityEntity? =
        activitiesCollection.document(activityId!!).get().await()
                .toObject(ActivityEntity::class.java)?.apply {
                    id = activityId
                }

    override suspend fun toggleStatusActivity(activityId: String, status: String): Resource<Boolean> {
        return try {
            activitiesCollection.document(activityId)
                .update(ActivityEntity.STATUS, status).await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun addInstrumentToActivity(instrumentId: String, activityId: String?): Resource<Boolean> {
        return try {
            instrumentsCollection.document(instrumentId)
                .update(InstrumentEntity.ACTIVITIES, FieldValue.arrayUnion(activityId)).await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun deleteInstrumentToActivity(instrumentId: String, activityId: String?): Resource<Boolean> {
        return try {
            instrumentsCollection.document(instrumentId)
                .update(InstrumentEntity.ACTIVITIES, FieldValue.arrayRemove(activityId)).await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun addUserToActivity(userId: String, activityId: String): Resource<Boolean> {
        return try {
            activitiesCollection.document(activityId).update(mapOf(ActivityEntity.USERS to FieldValue.arrayUnion(userId))).await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun deleteUserToActivity(userId: String, activityId: String): Resource<Boolean> {
        return try {
            activitiesCollection.document(activityId).update(mapOf(ActivityEntity.USERS to FieldValue.arrayRemove(userId))).await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getInstrumentSelected(instrumentId: String): InstrumentEntity? =
        instrumentsCollection.document(instrumentId).get().await()
            .toObject(InstrumentEntity::class.java)?.apply {
                id = instrumentId
            }

    override suspend fun getInstrumentsFlow(companyId: String, activityId: String?, businessId: String?): Flow<Resource<List<InstrumentEntity>>> = callbackFlow{
        if (companyId.isEmpty()) {
            awaitCancellation()
        }
//        activitiesCollection.get().await().documents.forEach { documentSnapshot ->
//            documentSnapshot.reference.update("companyAppId", companyId).await()
//        }
//        instrumentsCollection.get().await().documents.forEach { documentSnapshot ->
//            documentSnapshot.reference.update("companyAppId", companyId).await()
//        }
//        companiesCollection.get().await().documents.forEach { documentSnapshot ->
//            documentSnapshot.reference.update("companyAppId", companyId).await()
//        }
//        certificatesCollection.get().await().documents.forEach { documentSnapshot ->
//            documentSnapshot.reference.update("companyAppId", companyId).await()
//        }
//        patternsCollection.get().await().documents.forEach { documentSnapshot ->
//            documentSnapshot.reference.update("companyAppId", companyId).await()
//        }


        val instrumentsRef = if (!activityId.isNullOrEmpty()||!businessId.isNullOrEmpty()){
            instrumentsCollection.where(
                Filter.and(
                    Filter.equalTo(CertificateEntity.COMPANY_APP_ID, companyId),
                    if (!activityId.isNullOrEmpty())
                        Filter.arrayContains(InstrumentEntity.ACTIVITIES, activityId)
                    else
                        Filter.equalTo(InstrumentEntity.BUSINESS_ID, businessId)
                )
            )
        }else{
            instrumentsCollection.where(Filter.equalTo(CertificateEntity.COMPANY_APP_ID, companyId))
        }

        val subscription = instrumentsRef.addSnapshotListener { snapshot, exception ->
            if (exception != null){
                trySend(Resource.Error(exception.message.toString()))
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull {documentSnapshot ->
                documentSnapshot.toObject(InstrumentEntity::class.java)?.apply {
                    id = documentSnapshot.id
                }
            } ?: emptyList()
            trySend(Resource.Success(items))
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveInstrument(instrument: Map<String, Any>) {
        instrumentsCollection.add(instrument).await()
    }

    override suspend fun updateInstrument(instrument: Map<String, Any>, instrumentId: String): Resource<Boolean> {
        return try {
            instrumentsCollection.document(instrumentId).update(instrument).await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun updateCalibrationInfo(
        instrumentId: String, activityId: String?,
        calibrationInfo: Map<String, Any>): Resource<Boolean> {
        return try {
            val batch = firestore.batch()
            for ((key, value) in calibrationInfo){
                batch.update(instrumentsCollection.document(instrumentId), "${InstrumentEntity.CALIBRATIONS}.${activityId}.${key}", value)
            }
            batch.commit()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun deleteInstrument(instrumentId: String): Resource<Boolean> {
        return try {
            instrumentsCollection.document(instrumentId).delete().await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getUsersFlow(companyId: String): Flow<Resource<List<UserEntity>>> = callbackFlow {
        if (companyId.isEmpty()) {
            awaitCancellation()
        }
        val usersRef = companiesAppCollection.document(companyId).collection("users")
        val subscription = usersRef.addSnapshotListener { snapshot, exception ->
            if (exception != null){
                trySend(Resource.Error(exception.message.toString()))
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull {documentSnapshot ->
                documentSnapshot.toObject(UserEntity::class.java)?.apply {
                    id = documentSnapshot.id
                }
            } ?: emptyList()
            trySend(Resource.Success(items))
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun sendInvitedUser(userEmail: String, invitation: Map<String, Any>) {
        val querySnapshot = usersCollection.whereEqualTo(UserEntity.EMAIL, userEmail).get().await()
        querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.reference.update("invitations", FieldValue.arrayUnion(invitation)).await()
        }
    }

    override suspend fun updateUser(companyId: String, user: Map<String, Any>, userId: String): Resource<Boolean> {
        return try {
            companiesAppCollection.document(companyId).collection("users").document(userId).update(user).await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun deleteUser(companyId: String, userId: String): Resource<Boolean> {
        return try {
            companiesAppCollection.document(companyId).collection("users").document(userId).delete().await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getUserSelected(companyId: String, userId: String): UserEntity? =
        companiesAppCollection.document(companyId).collection("users").document(userId).get().await()
            .toObject(UserEntity::class.java)?.apply {
                id = userId
            }

    override suspend fun getCompanySelected(companyId: String): CompanyEntity? =
        companiesCollection.document(companyId).get().await()
            .toObject(CompanyEntity::class.java)?.apply {
                id = companyId
            }

    override suspend fun saveCompany(company: Map<String, Any>){
        companiesCollection.add(company).await()
    }

    override suspend fun updateCompany(company: Map<String, Any>, companyId: String): Resource<Boolean> {
        return try {
            companiesCollection.document(companyId).update(company).await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun deleteCompany(companyId: String): Resource<Boolean> {
        return try {
            companiesCollection.document(companyId).delete().await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getCompaniesFlow(companyId: String): Flow<Resource<List<CompanyEntity>>> = callbackFlow {
        if (companyId.isEmpty()) {
            awaitCancellation()
        }
        val companiesRef = firestore.collection(COMPANY_COLLECTION)
            .where(Filter.equalTo(CertificateEntity.COMPANY_APP_ID, companyId))
        val subscription = companiesRef.addSnapshotListener { snapshot, exception ->
            if (exception != null){
                trySend(Resource.Error(exception.message.toString()))
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull {documentSnapshot ->
                documentSnapshot.toObject(CompanyEntity::class.java)?.apply {
                    id = documentSnapshot.id
                }
            } ?: emptyList()
            trySend(Resource.Success(items))
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun getBusiness(companyId: String): List<CompanyEntity> {
        val querySnapshot = companiesCollection.where(
            Filter.equalTo(CompanyEntity.COMPANY_APP_ID, companyId)).get().await()
        val items = querySnapshot.documents.mapNotNull {documentSnapshot ->
            documentSnapshot.toObject(CompanyEntity::class.java)?.apply {
                id = documentSnapshot.id
            }
        }
        return items
    }

    override suspend fun getPatternSelected(patternId: String?): PatternEntity? =
        patternsCollection.document(patternId!!).get().await()
            .toObject(PatternEntity::class.java)?.apply {
                id = patternId
            }

    override suspend fun savePattern(pattern: Map<String, Any>) {
        patternsCollection.add(pattern).await()
    }

    override suspend fun updatePattern(pattern: Map<String, Any>, patternId: String) {
        patternsCollection.document(patternId).update(pattern).await()
    }

    override suspend fun deletePattern(patternId: String): Resource<Boolean> {

        return try {
            patternsCollection.document(patternId).delete().await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getAllPatterns(companyId: String): List<PatternEntity> {
        val querySnapshot = patternsCollection.where(
            Filter.equalTo(CompanyEntity.COMPANY_APP_ID, companyId)).get().await()
        val items = querySnapshot.documents.mapNotNull {documentSnapshot ->
            documentSnapshot.toObject(PatternEntity::class.java)?.apply {
                id = documentSnapshot.id
            }
        }
        return items
    }

    override suspend fun getPatternsFlow(companyId: String): Flow<Resource<List<PatternEntity>>> = callbackFlow {
        if (companyId.isEmpty()) {
            awaitCancellation()
        }
        val patternsRef = patternsCollection.where(Filter.equalTo(CertificateEntity.COMPANY_APP_ID, companyId))
        val subscription = patternsRef.addSnapshotListener { snapshot, exception ->
            if (exception != null){
                trySend(Resource.Error(exception.message.toString()))
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull {documentSnapshot ->
                documentSnapshot.toObject(PatternEntity::class.java)?.apply {
                    id = documentSnapshot.id
                }
            } ?: emptyList()
            trySend(Resource.Success(items))
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun getCertificateSelected(certificateId: String?): CertificateEntity? =
        certificatesCollection.document(certificateId!!).get().await()
            .toObject(CertificateEntity::class.java)?.apply {
                id = certificateId
            }

    override suspend fun saveCertificate(certificate: Map<String, Any>) {
        certificatesCollection.add(certificate).await()
    }

    override suspend fun updateCertificate(certificate: Map<String, Any>, certificateId: String) {
        patternsCollection.document(certificateId).update(certificate).await()
    }

    override suspend fun deleteCertificate(certificateId: String): Resource<Boolean> {
        return try {certificatesCollection.document(certificateId).delete().await()
            Resource.Success(true)

        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getAllCertificates(companyId: String): List<CertificateEntity> {
        val querySnapshot = certificatesCollection.where(Filter.equalTo(CertificateEntity.COMPANY_APP_ID, companyId)).get().await()
        val items = querySnapshot.documents.mapNotNull {documentSnapshot ->
            documentSnapshot.toObject(CertificateEntity::class.java)?.apply {
                id = documentSnapshot.id
            }
        }
        return items
    }

    override suspend fun getCertificatesFlow(companyId: String): Flow<Resource<List<CertificateEntity>>> = callbackFlow {
        if (companyId.isEmpty()) {
            awaitCancellation()
        }
        val subscription = certificatesCollection
            .where(Filter.equalTo(CertificateEntity.COMPANY_APP_ID, companyId))
            .addSnapshotListener { snapshot, exception ->
            if (exception != null){
                trySend(Resource.Error(exception.message.toString()))
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull {documentSnapshot ->
                documentSnapshot.toObject(CertificateEntity::class.java)?.apply {
                    id = documentSnapshot.id
                }
            } ?: emptyList()
            trySend(Resource.Success(items))
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun getFamilies(companyId: String): List<String> {
        val querySnapshot = certificatesCollection.where(
            Filter.equalTo(CompanyEntity.COMPANY_APP_ID, companyId)).get().await()
        val items = querySnapshot.toObjects(CertificateEntity::class.java)
            .map { it.family }.distinct().toMutableList().apply {
                add("Otro")
            }
        return items
    }

    override suspend fun getImagesFlow(instrumentId: String): Flow<Resource<List<ImageEntity>>> = callbackFlow {
        val imagesRef = instrumentsCollection.document(instrumentId).collection(IMAGES_COLLECTION)
        val subscription = imagesRef.addSnapshotListener { snapshot, exception ->
            if (exception != null){
                trySend(Resource.Error(exception.message.toString()))
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull {documentSnapshot ->
                documentSnapshot.toObject(ImageEntity::class.java)?.apply {
                    id = documentSnapshot.id
                }
            } ?: emptyList()
            trySend(Resource.Success(items))
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveImage(image: Map<String, Any>, instrumentId: String): Resource<String> {
        return try {
            val id = instrumentsCollection.document(instrumentId).collection("images").add(image).await().id
            Resource.Success(id)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun updateImage(image: Map<String, Any>, imageId: String, instrumentId: String) {
        instrumentsCollection.document(instrumentId).collection("images")
            .document(imageId).update(image).await()
    }

    override suspend fun deleteImage(imageId: String, instrumentId: String): Resource<Boolean> {
        return try {
            instrumentsCollection.document(instrumentId).collection(IMAGES_COLLECTION).document(imageId).delete().await()
            Resource.Success(true)
        }catch (e:Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getInvitationsFlow(userId: String): Flow<Resource<List<Map<String,Any>>?>> = callbackFlow{
        if (userId.isEmpty()){
            awaitCancellation()
        }
        val subscription = usersCollection.document(userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null){
                    trySend(Resource.Error(exception.message.toString()))
                    return@addSnapshotListener
                }
                val invitations = snapshot?.toObject(UserEntity::class.java)?.invitations
                trySend(Resource.Success(invitations))
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun aceptInvitation(user: Map<String, Any?>, accept: Boolean, userId: String, invitation: Map<String, Any>) {
        val companyId = invitation["companyId"] as String
        if (accept){
            companiesAppCollection.document(companyId).collection("users")
                .document(userId).set(user).await()
            companiesAppCollection.document(companyId).update("users", FieldValue.arrayUnion(userId)).await()
        }
        usersCollection.document(userId).update("invitations", FieldValue.arrayRemove(invitation)).await()
    }
}