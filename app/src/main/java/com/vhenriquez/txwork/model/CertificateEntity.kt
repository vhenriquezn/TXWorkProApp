package com.vhenriquez.txwork.model

import android.os.Parcel
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class CertificateEntity(
    @Exclude
    var id: String = "",
    
    var name: String = "",
    var companyAppId: String = "",
    var certificateId: String = "",
    
    var laboratory: String = "",
    
    var broadcastDate: String = "",
    
    @get:Exclude
    @set:Exclude
    var days : Int = 0,
    
    var pdfUrl: String = "",
    
    var family: String = ""
) : Serializable {

    fun doesMatchSearchQuery(query: String): Boolean {
        if (query.isBlank()) return false

        val lowerCaseQuery = query.lowercase()
        val matchingCombinations = listOf(
            name
        )

        return matchingCombinations.any {
            it.lowercase().contains(lowerCaseQuery)
        }
    }

    companion object {
        const val CERTIFICATE = "certificate"
        const val ID = "id"
        const val NAME = "name"
        const val CERTIFICATE_ID = "certificateId"
        const val COMPANY_APP_ID = "companyAppId"
        const val LABORATORY = "laboratory"
        const val BROADCAST_DATE = "broadcastDate"
        const val PDF_URL = "pdfUrl"
        const val FAMILY = "family"

    }

//    private constructor(parcel: Parcel) : this(
//        parcel.readString().toString(),
//        parcel.readString().toString(),
//        parcel.readString().toString(),
//        parcel.readString().toString(),
//        parcel.readString().toString(),
//        parcel.readInt(),
//        parcel.readString().toString(),
//        parcel.readString().toString()
//    )

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as CertificateEntity
//
//        if (id != other.id) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return id.hashCode()
//    }

}



