package com.vhenriquez.txwork.screens.home

import android.net.Uri
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.InstrumentEntity

data class HomeUiState(
    val title: String = "Actividades",
    val showLogoutDialog: Boolean = false,
    val showNotifications: Boolean = false,

)