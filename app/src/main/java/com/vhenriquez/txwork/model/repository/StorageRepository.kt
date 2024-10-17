package com.vhenriquez.txwork.model.repository

import android.net.Uri
import androidx.compose.runtime.MutableState
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CalibrationEntity
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.PatternEntity
import com.vhenriquez.txwork.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface StorageRepository {

    /////IMAGES/////////////////////////////////////////////////////////
    suspend fun uploadImage(imageUri: Uri, tag:String): Resource<String>

    /////CERTIFICATES///////////////////////////////////////////////////
    suspend fun uploadCertificate(certificateUri: Uri): Resource<String>

    suspend fun deleteFileFromUrl(path: String): Resource<Boolean>

}