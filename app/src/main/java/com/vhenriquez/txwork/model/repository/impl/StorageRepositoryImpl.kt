package com.vhenriquez.txwork.model.repository.impl

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.await
import com.vhenriquez.txwork.model.repository.StorageRepository
import com.vhenriquez.txwork.utils.Constants.Companion.PATH_CERTIFICATES_PDF
import com.vhenriquez.txwork.utils.Constants.Companion.PATH_INSTRUMENTS_PHOTO
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val firestore : FirebaseFirestore,
    private val storage : FirebaseStorage
) : StorageRepository {

    private val storageImagesRef
        get() = storage.reference.child(PATH_INSTRUMENTS_PHOTO)

    private val storageCertificatesRef
        get() = storage.reference.child(PATH_CERTIFICATES_PDF)

    override suspend fun uploadImage(imageUri: Uri, tag:String): Resource<String> {
        return try {
            val downloadUrl = storageImagesRef.child(tag)
                .putFile(imageUri).addOnProgressListener {

                }.await().storage.downloadUrl.await()
            Resource.Success(downloadUrl.toString())
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun deleteFileFromUrl(path: String): Resource<Boolean> {
        return try {
            storage.getReferenceFromUrl(path).delete().await()
            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun uploadCertificate(certificateUri: Uri): Resource<String> {
        return try {
            val downloadUrl = storageCertificatesRef.child("Manometros").child("Mi certificado")
                .putFile(certificateUri).addOnProgressListener {

                }.await().storage.downloadUrl.await()
            Resource.Success(downloadUrl.toString())
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }
}