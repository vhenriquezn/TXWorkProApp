package com.vhenriquez.txwork.screens.movements

import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CalibrationEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.InstrumentEntity

data class InstrumentDetailUiState(
    val showDialogObservationsCalibration: Boolean = false,
    val showDialogObservationsInstrument: Boolean = false,
    val showDialogEditInfo: Boolean = false,
    val showDialogCalibration: Boolean = false,
    val showDialogService: Boolean = false,
    val showDialogCalibrationType: Boolean = false,
    val showDialogPatterns: Boolean = false,

)