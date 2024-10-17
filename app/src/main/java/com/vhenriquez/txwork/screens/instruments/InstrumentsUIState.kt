package com.vhenriquez.txwork.screens.instruments

import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.InstrumentEntity

data class InstrumentsUiState(
    //val instruments: List<InstrumentEntity> = emptyList(),
    val selectedInstrument: InstrumentEntity? = null,
    val business: List<CompanyEntity> = emptyList(),
    val showDeleteInstrumentDialog: Boolean = false
)