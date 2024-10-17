package com.vhenriquez.txwork.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class CompanyEntity(
    @Exclude
    var id : String = "",
    var name: String = "",
    var address: String = "",
    var website: String = "",
    var companyAppId: String = "",
    var ownerId: String = "",
    var users : MutableList<String> = mutableListOf(),
    var logo: String? = null) : Serializable {

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
        const val USERS = "users"
        const val COMPANY = "company"
        const val COMPANY_APP_ID = "companyAppId"
        const val LOGO = "logo"
        const val NAME = "name"
        const val WEBSITE = "website"
        const val ADDRESS = "address"
        const val OWNER_ID = "ownerId"
    }
    }
