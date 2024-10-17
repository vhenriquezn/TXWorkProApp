package com.vhenriquez.txwork.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class EnterpriseEntity(
    var userName: String = "",
    var email: String = "",
    var photoUrl: String = "",
    @field:JvmField
    @get:Exclude
    var exist: Boolean = false,
    @get:Exclude
    var id: String = "",
    @get:Exclude
    var position : String = "",
    @get:Exclude
    var hhDay : String = "",
    @get:Exclude
    var hh50 : String = "",
    @get:Exclude
    var hh100 : String = ""){
    companion object {
        val USERNAME = "username"
        val PHOTO_URL = "photoUrl"
        val EMAIL = "email"
        val USERS = "users"
        val EXIST = "exist"
        val POSITION = "position"
        val HH_DAY = "hhDay"
        val HH_50 = "hh50"
        val HH_100 = "hh100"
    }

}
