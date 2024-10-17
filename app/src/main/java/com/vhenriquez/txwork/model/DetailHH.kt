package com.vhenriquez.txwork.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable
import kotlin.Any
@IgnoreExtraProperties
data class DetailHH(
    @get:Exclude
    var id: String = "",
    var date: String = "",
    var name: String = "",
    var hh: String = "",
    var day : String = "",
    var position: String = "",
    var place: String= "",
    val ot: String = "",
    val costTools: String= "",
    val oc: String= "",
    val activity: String= "",
    val activityId: String= "") : Serializable {
    companion object {
        const val ID = "id"
        const val DATE = "date"
        const val NAME = "name"
        const val HH = "hh"
        const val HH50 = "hh50"
        const val HH100 = "hh100"
        const val POSITION = "position"
        const val PLACE = "place"
        const val OT = "ot"
        const val COST_TOOLS = "costTools"
        const val OC = "oc"
        const val ACTIVITY = "activity"
        const val ACTIVITY_ID = "activityId"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DetailHH

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
