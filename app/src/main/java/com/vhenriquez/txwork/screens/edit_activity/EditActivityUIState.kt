package com.vhenriquez.txwork.screens.edit_activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.InstrumentEntity

data class EditActivityUiState(
    val expandedBusiness: Boolean = false,
    val showDialogPicker: Boolean = false
)
