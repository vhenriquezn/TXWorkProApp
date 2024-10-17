package com.vhenriquez.txwork.screens.edit_instrument

data class EditInstrumentUiState(
    val setRange: Boolean = false,
    val unitOptionsPv: List<String> = emptyList(),
    val isVisibilityMagnitude: Boolean = true,
    val showDialogNewVerificationUnit: Boolean = false
)