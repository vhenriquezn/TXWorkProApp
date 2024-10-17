package com.vhenriquez.txwork.screens.edit_certificate

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.AddItemDialog
import com.vhenriquez.txwork.common.composable.CustomDatePickerDialog
import com.vhenriquez.txwork.common.composable.EditTextInDialog
import com.vhenriquez.txwork.common.composable.PopupBox
import com.vhenriquez.txwork.model.CertificateEntity

@Composable
fun EditCertificateScreen(
    popUp: () -> Unit,
    viewModel: EditCertificateViewModel = hiltViewModel()){

    EditCertificateScreenContent(
        viewModel = viewModel,
        popUp = popUp,
        onDismiss = {popUp()}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCertificateScreenContent(
    viewModel: EditCertificateViewModel,
    popUp: () -> Unit,
    onDismiss: () -> Unit,
){
    val certificate by viewModel.certificate
    val families by viewModel.families.collectAsState()
    val uiState by viewModel.uiState
    val launcherPDF = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri?->
        viewModel.onCertificateUrlChange(uri.toString())
    }

    PopupBox(
        title = if (certificate.id.isEmpty()) stringResource(id = R.string.add_certificate_dialog_title)
        else stringResource(id = R.string.edit_certificate_dialog_title),
        onDismiss = onDismiss,
        onConfirm = {viewModel.onSaveData(popUp)})
    {
        Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween) {
            EditTextInDialog(
                value = certificate.name,
                onValueChange = viewModel::onNameChange,
                label = stringResource(id = R.string.add_certificate_dialog_name)
            )
            EditTextInDialog(
                value = certificate.laboratory,
                onValueChange = viewModel::onLaboratoryChange,
                label = stringResource(id = R.string.add_certificate_dialog_laboratory)
            )
            ExposedDropdownMenuBox(
                expanded = uiState.expandedFamily,
                onExpandedChange = {
                    viewModel.uiState.value = uiState.copy(expandedFamily = !uiState.expandedFamily)
                })
            {
                OutlinedTextField(
                    value = certificate.family,
                    label = { Text(text = stringResource(id = R.string.add_certificate_dialog_family)) },
                    onValueChange = {  },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedFamily) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = uiState.expandedFamily,
                    onDismissRequest = { viewModel.uiState.value = uiState.copy(expandedFamily = false)}
                ) {
                    families.forEachIndexed { index, family ->
                        DropdownMenuItem(
                            text = {
                                Column(verticalArrangement = Arrangement.SpaceBetween){
                                    Text(text = family)
                                    HorizontalDivider()
                                }
                                   },
                            onClick = {
                                if (index == families.lastIndex){
                                    viewModel.uiState.value = uiState.copy(showDialogNewFamily = true)
                                }else
                                    viewModel.onFamilyChange(family)
                            }
                        )
                    }
                }
            }

            EditTextInDialog(
                value = certificate.certificateId,
                onValueChange = viewModel::onCertificateIdChange,
                label = stringResource(id = R.string.add_certificate_dialog_certificate_id))

            OutlinedTextField(
                value = certificate.broadcastDate,
                onValueChange = viewModel::onDateChange,
                trailingIcon = {
                    IconButton(onClick = { viewModel.uiState.value = uiState.copy(showDialogPicker = true)}){
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "calendarIcon")}
                },
                label = { Text(text = stringResource(id = R.string.add_certificate_dialog_date)) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            ElevatedButton(onClick = { launcherPDF.launch("application/pdf") }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(stringResource(R.string.certificate_dialog_selectCertificate))
            }
            AnimatedVisibility(uiState.showDialogPicker) {
                CustomDatePickerDialog(
                    onChangeShowDialogPicker = {viewModel.uiState.value = uiState.copy(showDialogPicker = it)},
                    onDateChange = viewModel::onDateChange
                )
            }
            AnimatedVisibility(uiState.showDialogNewFamily) {
                AddItemDialog(
                    title = stringResource(id = R.string.add_certificate_dialog_new_family),
                    value = "",
                    onDismiss = { viewModel.uiState.value = uiState.copy(showDialogNewFamily =false) },
                    onConfirm = viewModel::onFamilyChange
                )
            }
        }
    }
}