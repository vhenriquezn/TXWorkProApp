package com.vhenriquez.txwork.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class PatternEntity(
    @Exclude
    var id: String = "",

    var name: String = "",

    var serial: String = "",
    var companyAppId: String = "",
    var brand: String = "",

    var certificateId : String = "",

    var certificate : CertificateEntity = CertificateEntity(),
    var isChecked: Boolean = false
):Serializable{
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
        const val PATTERN = "pattern"
        const val ID = "id"
        const val NAME = "name"
        const val SERIAL = "serial"
        const val COMPANY_APP_ID = "companyAppId"
        const val BRAND = "brand"
        const val CERTIFICATE = "certificate"
        const val CERTIFICATE_ID = "certificateId"
    }
}
