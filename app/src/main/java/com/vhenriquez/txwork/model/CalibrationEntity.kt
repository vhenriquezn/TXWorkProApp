package com.vhenriquez.txwork.model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable
import kotlin.Any

@IgnoreExtraProperties
data class CalibrationEntity(

    var calibrationType: String = "VERIFICACION Y CORRECCION",

    var service: String = "VERIFICACION TERRENO",

    var calibrationValues: Map<String, Map<String,String>>? = mapOf(),

    var observation: String = "",

    var patternEntities: MutableList<PatternEntity> = mutableListOf(),

    val disassemblyDate: String = "",

    var disassemblyInst : String = "",

    var calibrateDate: String = "",

    var calibrateInst: String = "",

    val mountDate: String = "",

    var mountInst: String = ""
): Serializable {
    companion object {
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
}
