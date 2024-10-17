package com.vhenriquez.txwork.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.vhenriquez.txwork.screens.movements.Calibration
import com.vhenriquez.txwork.screens.movements.Information
import com.vhenriquez.txwork.screens.movements.DetailInstrumentViewModel

sealed class ItemsTabsMovements(
    var title: String,
    var iconSelected: ImageVector,
    var iconUnselected: ImageVector,
    var screen: @Composable () -> Unit
){
    data class TabInformation(val titleTab: String, val viewModel: DetailInstrumentViewModel): ItemsTabsMovements(
        titleTab,
        Icons.Filled.Info,
        Icons.Outlined.Info,
        { Information(viewModel) })

    data class TabCalibration(val titleTab: String, val viewModel: DetailInstrumentViewModel): ItemsTabsMovements(
        titleTab,
        Icons.Filled.Settings,
        Icons.Outlined.Settings,
        { Calibration(viewModel) })

}