package com.vhenriquez.txwork.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.Gson
import java.io.Serializable
@IgnoreExtraProperties
data class InstrumentEntity(
    var id: String = "",
    var tag: String = "",
    var business: String = "",
    var businessId: String = "",
    var description: String = "",
    var area: String = "",
    var instrumentType: String = "",
    var magnitude: String = "",
    var verificationMin: String = "",
    var verificationMax: String = "",
    var verificationUnit: String = "",
    var reportType: String = "",
    var companyAppId: String = "",
    var brand: String = "",
    var model: String = "",
    var serial: String = "",
    var damping: String = "",
    var measurementMin: String = "",
    var measurementMax: String = "",
    var measurementUnit: String = "",
    var output: String = "",
    var sensorType: String = "",

    var sensorModel: String = "",
    var verificationMinSV: String = "",
    var verificationMaxSV: String = "",
    var verificationUnitSV: String = "",
    var verificationMinTV: String = "",
    var verificationMaxTV: String = "",
    var verificationUnitTV: String = "",
    var verificationMinQV: String = "",
    var verificationMaxQV: String = "",
    var verificationUnitQV: String = "",
    var address: String = "",
    var sensorDiameter: String = "",
    var usefulLength: String = "",
    var diameter: String = "",
    var resolution: String = "",
    var processConnection: String = "",
    var observations: String = "",
    var images : MutableList<ImageEntity> = mutableListOf(),
    val calibrations: MutableMap<String, CalibrationEntity> = mutableMapOf(),
    val activities: List<String> = mutableListOf(),
    val aditionalInfoSensor: MutableMap<String, String> = mutableMapOf(),
    @Exclude
    var isSelected: Boolean = false,
    val states : MutableMap<String, Int> = mutableMapOf()
    //val status : MutableMap<String, MutableMap<String, Boolean>> = mutableMapOf()
) : Serializable {
    fun doesMatchSearchQuery(query: String): Boolean {
        if (query.isBlank()) return false

        val lowerCaseQuery = query.lowercase()
        val matchingCombinations = listOf(
            tag,
            business,
        )

        return matchingCombinations.any {
            it.lowercase().contains(lowerCaseQuery)
        }
    }
    companion object {
        const val INSTRUMENT = "instrument"
        const val ID = "id"
        const val TAG = "tag"
        const val BUSINESS = "business"
        const val BUSINESS_ID = "businessId"
        const val DESCRIPTION = "description"
        const val AREA = "area"
        const val BRAND = "brand"
        const val MODEL = "model"
        const val SERIAL = "serial"
        const val SENSOR_MODEL = "sensorModel"
        const val COMPANY_APP_ID = "companyAppId"
        const val DAMPING = "damping"
        const val MEASUREMENT_MIN = "measurementMin"
        const val MEASUREMENT_MAX = "measurementMax"
        const val MEASUREMENT_UNIT = "measurementUnit"
        const val VERIFICATION_MIN = "verificationMin"
        const val VERIFICATION_MAX = "verificationMax"
        const val VERIFICATION_UNIT = "verificationUnit"
        const val VERIFICATION_MIN_SV = "verificationMinSV"
        const val VERIFICATION_MAX_SV = "verificationMaxSV"
        const val VERIFICATION_UNIT_SV = "verificationUnitSV"
        const val VERIFICATION_MIN_TV = "verificationMinTV"
        const val VERIFICATION_MAX_TV = "verificationMaxTV"
        const val VERIFICATION_UNIT_TV = "verificationUnitTV"
        const val VERIFICATION_MIN_QV = "verificationMinQV"
        const val VERIFICATION_MAX_QV = "verificationMaxQV"
        const val VERIFICATION_UNIT_QV = "verificationUnitQV"
        const val OBSERVATIONS = "observations"
        const val OUTPUT = "output"
        const val ADDRESS = "address"
        const val INSTRUMENT_TYPE = "instrumentType"
        const val SENSOR_TYPE = "sensorType"
        const val SENSOR_DIAMETER = "sensorDiameter"
        const val DIAMETER = "diameter"
        const val RESOLUTION = "resolution"
        const val PROCESS_CONNECTION = "processConnection"
        const val USEFUL_LENGTH = "usefulLength"
        const val MAGNITUDE = "magnitude"
        const val CALIBRATIONS = "calibrations"
        const val ACTIVITIES = "activities"
        const val ADITIONAL_INFO_SENSOR = "aditionalInfoSensor"
        const val REPORT_TYPE = "reportType"
        const val STATES = "states"
    }

    fun getRangeInstrument() : String =
        if (measurementMin.isNotEmpty()) "$measurementMin @ $measurementMax [$measurementUnit]"
        else if (measurementMin.isEmpty() && measurementMax.isNotEmpty()) "$measurementMax [$measurementUnit]"
        else measurementUnit

    fun getRangeVerification() : String =
        if (instrumentType == "Switch") " $verificationMax [$verificationUnit]"
        else if (verificationMin.isNotEmpty()) "$verificationMin @ $verificationMax [$verificationUnit]"
        else if (verificationMin.isEmpty() && verificationMax.isNotEmpty()) "$verificationMax [$verificationUnit]"
        else measurementUnit

    fun getRangeVerificationSV() : String =
        if (verificationMinSV.isNotEmpty()) "$verificationMinSV @ $verificationMaxSV [$verificationUnitSV]"
        else "-"

    fun getRangeVerificationTV() : String =
        if (verificationMinTV.isNotEmpty()) "$verificationMinTV @ $verificationMaxTV [$verificationUnitTV]"
        else "-"

    fun getRangeVerificationQV() : String =
        if (verificationMinQV.isNotEmpty()) "$verificationMinQV @ $verificationMaxQV [$verificationUnitQV]"
        else "-"

    //fun getSeatedValue() : String = "$verificationMax $verificationUnit"

    fun getDampingInstrument() : String = if (damping.isNotEmpty()) "$damping [Seg]" else "-"

    fun getSpanInstrument() : String = "${getSpan()} [$verificationUnit]"


    fun getSpan() : Float{
        return if (verificationMin.isEmpty() || verificationMax.isEmpty())
            0f
        else
            (verificationMax.toFloat()-verificationMin.toFloat())
    }

    fun getSpanSV() : Float{
        return if (verificationMinSV.isEmpty() || verificationMaxSV.isEmpty())
            0f
        else
            (verificationMaxSV.toFloat()-verificationMinSV.toFloat())
    }

    fun getSpanTV() : Float{
        return if (verificationMinTV.isEmpty() || verificationMaxTV.isEmpty())
            0f
        else
            (verificationMaxTV.toFloat()-verificationMinTV.toFloat())
    }

    fun getSpanQV() : Float{
        return if (verificationMinQV.isEmpty() || verificationMaxQV.isEmpty())
            0f
        else
            (verificationMaxQV.toFloat()-verificationMinQV.toFloat())
    }

    fun getInstrumentForReport(activityId: String) : String{

        val gson = Gson()
        return gson.toJson(
            InstrumentEntity(
                id = id, tag = tag, area = area, description = description, brand = model, serial = serial,
                sensorModel = sensorModel, damping = damping, businessId = businessId, images = images,
                measurementMin = measurementMin, measurementMax = measurementMax, measurementUnit = measurementUnit,
                verificationMin = verificationMin, verificationMax = verificationMax, verificationUnit =  verificationUnit,
                verificationMinSV = verificationMinSV, verificationMaxSV =  verificationMaxSV, verificationUnitSV = verificationUnitSV,
                verificationMinTV = verificationMinTV, verificationMaxTV = verificationMaxTV, verificationUnitTV = verificationUnitTV,
                verificationMinQV = verificationMinQV, verificationMaxQV = verificationMaxQV, verificationUnitQV = verificationUnitQV,
                output = output, address = address, instrumentType = instrumentType, sensorType = sensorType,
                sensorDiameter = sensorDiameter, usefulLength =  usefulLength, magnitude = magnitude, diameter = diameter,
                resolution = resolution, processConnection = processConnection, business = business,
                observations = observations, activities = activities, aditionalInfoSensor = aditionalInfoSensor, reportType = reportType,
                calibrations = mutableMapOf(activityId to calibrations.getOrDefault(activityId, CalibrationEntity())),
                isSelected = isSelected, states = states)
        )
    }

}
