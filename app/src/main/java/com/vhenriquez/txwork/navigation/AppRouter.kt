package com.vhenriquez.txwork.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Auth{
    @Serializable
    data object Splash

    @Serializable
    data object Login

    @Serializable
    data object SignUp

    @Serializable
    data object ForgotPassword
}

@Serializable
data object Main{
    @Serializable
    data object Home

    @Serializable
    data class ActivityDetail(val activityId: String, val companyAppId: String)
    @Serializable
    data class EditActivity(val activityId: String? = null, val companyAppId: String)
    @Serializable
    data class AddInstrumentsToActivity(val activityId: String? = null, val businessId: String? = null, val companyAppId: String)
    @Serializable
    data class AddUsersToActivity(val activityId: String? = null, val companyAppId: String)
    @Serializable
    data class EditInstrument(val instrumentId: String? = null, val activityId: String? = null, val companyAppId: String)
    @Serializable
    data class InstrumentDetail(val instrumentId: String)
    @Serializable
    data class EditPattern(val patternId: String? = null, val companyAppId: String)
    @Serializable
    data class EditCompany(val companyId: String? = null, val companyAppId: String)
    @Serializable
    data class EditCompanyApp(val companyId: String? = null)
    @Serializable
    data class EditCertificate(val certificateId: String? = null, val companyAppId: String)
    @Serializable
    data class EditUser(val userId: String? = null, val companyAppId: String)
}

@Serializable
data object Drawer{
    @Serializable
    data class Activities(val companyAppId: String)
    @Serializable
    data class Instruments(val companyAppId: String)
    @Serializable
    data class Users(val companyAppId: String)
    @Serializable
    data class Business(val companyAppId: String)
    @Serializable
    data class Patterns(val companyAppId: String)
    @Serializable
    data class Certificates(val companyAppId: String)
    @Serializable
    data object CalculatorPV
    @Serializable
    data object CalculatorDP
}