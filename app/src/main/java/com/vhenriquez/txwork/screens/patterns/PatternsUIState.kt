package com.vhenriquez.txwork.screens.patterns

import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.PatternEntity

data class PatternsUiState(
    //val activities: List<ActivityEntity> = emptyList(),
    val selectedPattern: PatternEntity? = null,
    val showDeletePatternDialog: Boolean = false,
    //val expanded: Boolean = false
)