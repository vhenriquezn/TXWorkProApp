package com.vhenriquez.txwork.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable
import kotlin.Any

@IgnoreExtraProperties
data class CalibrationEntityreserva(

    @DocumentId val id: String = "",

    var instrumentId: String = "",

    var activityId: String = "",

    var calibrationType: String = "VERIFICACION Y CORRECCION TEMPERATURA",

    var service: String = "VERIFICACION TERRENO",

    val date: String = "",

    var calibrationValues: MutableMap<String, Any> = mutableMapOf(),

    var observation: String = "",

    var patternEntities: MutableList<PatternEntity> = mutableListOf(),

    val location: String = "",

    val disassemblyDate: String = "",

    val disassemblyInst : String = "",

    var calibrateDate: String = "",

    var calibrateInst: String = "",

    val mountDate: String = "",

    val mountInst: String = "",

    var status: Int = 0
): Serializable {
    companion object {
        const val ID = "id"
        const val INSTRUMENT_ID = "instrumentId"
        const val ACTIVITY_ID = "activityId"
        const val PATTERNS_ENTITY = "patternEntities"
        const val CALIBRATION_TYPE = "calibrationType"
        const val SERVICE = "service"
        const val CALIBRATION_VALUES = "calibrationValues"
        const val DATE = "date"

        const val OBSERVATION = "observation"
        const val STATUS = "status"
        const val LOCATION = "location"
        const val DISASSEMBLY_DATE = "disassemblyDate"
        const val DISASSEMBLY_INST = "disassemblyInst"
        const val CALIBRATE_DATE = "calibrateDate"
        const val CALIBRATE_INST = "calibrateInst"
        const val MOUNT_DATE = "mountDate"
        const val MOUNT_INST = "mountInst"
        const val CALIBRATION = "calibration"
    }

    fun getDisassemblyInstrumentist() : String = if (disassemblyInst.isNotEmpty()) "($disassemblyInst)" else ""

    fun getCalibrateInstrumnentist() : String = if (calibrateInst.isNotEmpty()) "($calibrateInst)" else ""

    fun getMountInstrumentist() : String = if (mountInst.isNotEmpty()) "($mountInst)" else ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CalibrationEntityreserva

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
