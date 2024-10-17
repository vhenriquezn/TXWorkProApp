package com.vhenriquez.txwork.screens.activities

import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity

data class ActivitiesUiState(
    val selectedActivity: ActivityEntity? = null,
    val business: List<CompanyEntity> = emptyList(),
    val showDeleteActivityDialog: Boolean = false,
)