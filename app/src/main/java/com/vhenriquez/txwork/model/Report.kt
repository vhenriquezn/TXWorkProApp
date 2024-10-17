package com.vhenriquez.txwork.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.io.Serializable
import kotlin.Any

data class Report(
    @DocumentId var id: String = "",
    var name: String = "",
    var activityId: String = "",
    var date: String = "",
    var ot: String = "",
    var supervisor: String = "",
    var planta: String = "",
    var ito: String = "",
    var service : String = "",
    var project: String = "",
    var users: MutableList<Map<String,String>> = mutableListOf(),
    var activities: MutableList<Map<String, String>> = mutableListOf(),
    var observations: MutableList<String> = mutableListOf(),
    ) : Serializable {
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val ACTIVITY_ID = "activityId"
        const val DATE = "date"
        const val OT = "ot"
        const val SUPERVISOR = "supervisor"
        const val PLANT = "plant"
        const val ITO = "ito"
        const val SERVICE = "service"
        const val PROJECT = "project"
        const val ACTIVITIES = "activities"
        const val USERS = "users"
        const val OBSERVATIONS = "observations"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Report

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
