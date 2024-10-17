package com.vhenriquez.txwork.model.repository

import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.ImageEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.PatternEntity
import com.vhenriquez.txwork.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    /////COMPANIES_APP//////////////////////////////////////////////////////
    suspend fun getCompanyAppSelected(companyId: String): CompanyEntity?
    suspend fun getUserInCompanyApp(companyId: String, userId: String): Map<String, Any>
    suspend fun saveCompanyApp(company: Map<String,Any>)
    suspend fun updateCompanyApp(company: Map<String, Any>, companyId: String): Resource<Boolean>
    suspend fun deleteCompanyApp(companyId: String): Resource<Boolean>
    suspend fun getCompaniesAppFlow(userId: String): Flow<Resource<List<CompanyEntity>>>

    /////ACTIVITIES/////////////////////////////////////////////////////
    suspend fun getActivitiesFlow(companyId: String, filter: String?, userId: String?): Flow<Resource<List<ActivityEntity>>>
    suspend fun saveActivity(activity: Map<String,Any>)
    suspend fun updateActivity(activity: Map<String,Any>, activityId: String)
    suspend fun deleteActivity(activityId: String): Resource<Boolean>
    suspend fun getActivitySelected(activityId: String?): ActivityEntity?
    suspend fun toggleStatusActivity(activityId: String, status: String): Resource<Boolean>
    suspend fun addInstrumentToActivity(instrumentId: String, activityId: String?): Resource<Boolean>
    suspend fun deleteInstrumentToActivity(instrumentId: String, activityId: String?): Resource<Boolean>
    suspend fun deleteUserToActivity(userId: String, activityId: String): Resource<Boolean>
    suspend fun addUserToActivity(userId: String, activityId: String): Resource<Boolean>
    /////INSTRUMENTS////////////////////////////////////////////////////
    suspend fun getInstrumentsFlow(companyId: String,activityId: String? = null, businessId: String? = null): Flow<Resource<List<InstrumentEntity>>>
    suspend fun saveInstrument(instrument: Map<String,Any>)
    suspend fun updateInstrument(instrument: Map<String,Any>, instrumentId: String): Resource<Boolean>
    suspend fun updateCalibrationInfo(instrumentId: String, activityId: String?, calibrationInfo: Map<String, Any>): Resource<Boolean>

    suspend fun deleteInstrument(instrumentId: String): Resource<Boolean>
    suspend fun getInstrumentSelected(instrumentId: String): InstrumentEntity?
    /////USERS//////////////////////////////////////////////////////////
    suspend fun getUsersFlow(companyId: String): Flow<Resource<List<UserEntity>>>
    suspend fun sendInvitedUser(userEmail: String, invitation: Map<String, Any>)
    suspend fun updateUser(companyId: String, user: Map<String,Any>, userId: String): Resource<Boolean>
    suspend fun deleteUser(companyId: String, userId: String): Resource<Boolean>
    suspend fun getUserSelected(companyId: String, userId: String): UserEntity?
    /////COMPANIES//////////////////////////////////////////////////////
    suspend fun getCompanySelected(companyId: String): CompanyEntity?
    suspend fun saveCompany(company: Map<String,Any>)
    suspend fun updateCompany(company: Map<String, Any>, companyId: String): Resource<Boolean>
    suspend fun deleteCompany(companyId: String): Resource<Boolean>
    suspend fun getCompaniesFlow(companyId: String): Flow<Resource<List<CompanyEntity>>>
    suspend fun getBusiness(companyId: String): List<CompanyEntity>
    /////PATTERNS///////////////////////////////////////////////////////
    suspend fun getPatternSelected(patternId: String?): PatternEntity?
    suspend fun savePattern(pattern: Map<String, Any>)
    suspend fun updatePattern(pattern: Map<String, Any>, patternId: String)
    suspend fun deletePattern(patternId: String): Resource<Boolean>
    suspend fun getAllPatterns(companyId: String): List<PatternEntity>?
    suspend fun getPatternsFlow(companyId: String): Flow<Resource<List<PatternEntity>>>
    /////CERTIFICATES///////////////////////////////////////////////////
    suspend fun getCertificateSelected(certificateId: String?): CertificateEntity?
    suspend fun saveCertificate(certificate: Map<String, Any>)
    suspend fun updateCertificate(certificate: Map<String, Any>, certificateId: String)
    suspend fun deleteCertificate(certificateId: String): Resource<Boolean>
    suspend fun getAllCertificates(companyId: String): List<CertificateEntity>?
    suspend fun getCertificatesFlow(companyId: String): Flow<Resource<List<CertificateEntity>>>
    suspend fun getFamilies(companyId: String): List<String>
    /////IMAGES////////////////////////////////
    suspend fun getImagesFlow(instrumentId: String): Flow<Resource<List<ImageEntity>>>
    suspend fun saveImage(image: Map<String, Any>, instrumentId: String): Resource<String>
    suspend fun updateImage(image: Map<String, Any>,imageId: String, instrumentId: String)
    suspend fun deleteImage(imageId: String, instrumentId: String): Resource<Boolean>
    ////INVITATIONS/////////////////////////////////////////////////////
    suspend fun getInvitationsFlow(userId: String): Flow<Resource<List<Map<String, Any>>?>>
    suspend fun aceptInvitation(user: Map<String, Any?>, accept: Boolean, userId: String, invitation: Map<String, Any>)


}