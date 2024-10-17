package com.vhenriquez.txwork.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserEntity(
    @DocumentId var id: String = "",
    var userName: String = "",
    var email: String? = "",
    var photoUrl: String? = "",
    var invitations: List<Map<String, Any>> = listOf(),
    val roles: Map<String, Boolean> = mapOf(),
    var userType :String = "Instrumentista",
    var plan : Int = 0,
    var caducidad : String = "",
    @get:Exclude
    var position : String = "",
    @get:Exclude
    var hhDay : String = ""){
    fun doesMatchSearchQuery(query: String): Boolean {
        if (query.isBlank()) return false

        val lowerCaseQuery = query.lowercase()
        val matchingCombinations = listOf(
            userName
        )

        return matchingCombinations.any {
            it.lowercase().contains(lowerCaseQuery)
        }
    }
    companion object {
        const val ID = "id"
        const val USERNAME = "userName"
        const val PHOTO_URL = "photoUrl"
        const val EMAIL = "email"
        const val PLAN = "plan"
        const val USERS = "users"
        const val ROLES = "roles"
        const val USER_TYPE = "userType"
        const val EXIST = "exist"
        const val POSITION = "position"
    }
}
