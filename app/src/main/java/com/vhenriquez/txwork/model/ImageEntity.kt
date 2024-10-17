package com.vhenriquez.txwork.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class ImageEntity(
    @Exclude
    var id : String = "",
    var name: String = "",
    var photoUrl: String = "",
    @Exclude
    var state : Int = 0,) : Serializable {
    companion object {
        const val IMAGE = "image"
        const val NAME = "name"
        const val STATE = "state"
        const val PHOTO_URL = "photoUrl"
        const val STATE_UPLOADING = 0
        const val STATE_UPLOAD = 1
        const val STATE_CANCELLED = 2
        const val STATE_SAVED = 3
    }

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as ImageEntity
//
//        return id == other.id
//    }
//
    override fun hashCode(): Int {
        return id.hashCode()
    }
}
