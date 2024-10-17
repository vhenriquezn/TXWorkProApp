package com.vhenriquez.txwork.screens.edit_activity

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.CustomDatePickerDialog
import com.vhenriquez.txwork.common.composable.DropdownSelector
import com.vhenriquez.txwork.common.composable.EditTextInDialog
import com.vhenriquez.txwork.common.composable.PopupBox
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.InstrumentEntity.Companion.TAG
import java.time.Instant
import java.time.ZoneId

@Composable
fun EditActivityScreen(
    popUp: () -> Unit,
    viewModel: EditActivityViewModel = hiltViewModel()){

    EditActivityScreenContent(
        viewModel = viewModel,
        onDismiss = {popUp()},
        popUp = popUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreenContent(
    viewModel: EditActivityViewModel,
    onDismiss: () -> Unit,
    popUp: () -> Unit
){
    val activityEntity by viewModel.activity
    val business by viewModel.business.collectAsState()
    val uiState by viewModel.uiState
    PopupBox(
        title = if(activityEntity.id.isEmpty())stringResource(id = R.string.add_activity_dialog_title)
        else stringResource(id = R.string.edit_activity_dialog_title),
        onDismiss = onDismiss,
        onConfirm = {viewModel.onSaveData(popUp)})
    {
        Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween) {
            EditTextInDialog(
                value = activityEntity.name,
                onValueChange = viewModel::onNameChange,
                stringResource(id = R.string.add_activity_dialog_name)
            )

            OutlinedTextField(
                value = activityEntity.date,
                onValueChange = viewModel::onDateChange,
                trailingIcon = {
                    IconButton(onClick = {viewModel.uiState.value = uiState.copy(showDialogPicker = true)}){
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "calendarIcon")
                    }
                },
                label = { Text(text = stringResource(id = R.string.add_activity_dialog_date)) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            AnimatedVisibility(uiState.showDialogPicker) {
                CustomDatePickerDialog(
                    onChangeShowDialogPicker = {viewModel.uiState.value = uiState.copy(showDialogPicker = it)},
                    onDateChange = viewModel::onDateChange
                )
            }

            ExposedDropdownMenuBox(
                expanded = uiState.expandedBusiness,
                onExpandedChange = {
                    viewModel.uiState.value = uiState.copy(expandedBusiness = !uiState.expandedBusiness)
                })
            {
                OutlinedTextField(
                    value = activityEntity.business,
                    label = { Text(text = stringResource(id = R.string.add_activity_dialog_business)) },
                    onValueChange = {  },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedBusiness) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = uiState.expandedBusiness,
                    onDismissRequest = { viewModel.uiState.value = uiState.copy(expandedBusiness = false)}
                ) {
                    business.forEachIndexed { index, companyEntity ->
                        DropdownMenuItem(
                            text = { Text(text = companyEntity.name) },
                            onClick = {
                                viewModel.onBusinessChange(companyEntity.name, companyEntity.id)
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EditTextInDialog(
                    value = activityEntity.workOrder,
                    onValueChange = viewModel::onWorkOrderChange,
                    label = "OT",
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.width(6.dp))
                EditTextInDialog(
                    value = activityEntity.serviceOrder,
                    onValueChange = viewModel::onServiceOrderChange,
                    label = "OC/OS",
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }
}