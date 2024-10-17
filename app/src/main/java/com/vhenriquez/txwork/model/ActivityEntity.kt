package com.vhenriquez.txwork.model

import androidx.compose.ui.text.intl.Locale
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
@Serializable
data class ActivityEntity(
    @Exclude var id: String = "",
    var name: String = "",
    var date: String = "",
    var business: String = "",
    var businessId: String = "",
    var companyAppId: String = "",
    var status : String = "open",
    var ownerId: String = "",
    var ownerName: String = "",
    var workOrder : String = "",
    var serviceOrder : String = "",
    var users: MutableList<String> = mutableListOf()) {

    fun doesMatchSearchQuery(query: String): Boolean {
        if (query.isBlank()) return false

        val lowerCaseQuery = query.lowercase()
        val matchingCombinations = listOf(
            name,
            business,
            workOrder,
            serviceOrder
        )

        return matchingCombinations.any {
            it.lowercase().contains(lowerCaseQuery)
        }
    }

    companion object {
        const val ACTIVITY = "activity"
        const val ID = "id"
        const val NAME = "name"
        const val DATE = "date"
        const val STATUS = "status"
        const val BUSINESS = "business"
        const val BUSINESS_ID = "businessId"
        const val COMPANY_APP_ID = "companyAppId"
        const val OWNER_ID = "ownerId"
        const val OWNER_NAME = "ownerName"
        const val USERS = "users"
        const val WORK_ORDER = "workOrder"
        const val SERVICE_ORDER = "serviceOrder"
        const val ACTIVITY_ID = "activityId"
    }

}
