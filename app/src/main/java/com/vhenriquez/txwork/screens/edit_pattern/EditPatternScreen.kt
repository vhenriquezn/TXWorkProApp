package com.vhenriquez.txwork.screens.edit_pattern

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.EditTextInDialog
import com.vhenriquez.txwork.common.composable.PopupBox
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.PatternEntity

@Composable
fun EditPatternScreen(
    popUp: () -> Unit,
    viewModel: EditPatternViewModel = hiltViewModel()){


    EditPatternScreenContent(
        viewModel = viewModel,
        popUp = popUp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPatternScreenContent(
    viewModel: EditPatternViewModel,
    popUp: () -> Unit
){
    val pattern by viewModel.pattern
    val certificates by viewModel.certificates.collectAsState()
    val uiState by viewModel.uiState
    PopupBox(
        title = if (pattern.id.isEmpty()) stringResource(id = R.string.add_pattern_dialog_title)
        else stringResource(id = R.string.edit_pattern_dialog_title),
        onDismiss = {popUp()},
        onConfirm = {viewModel.onSaveData(popUp)})
    {
        Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween) {
            EditTextInDialog(
                value = pattern.name,
                onValueChange = viewModel::onNameChange,
                label = stringResource(id = R.string.add_pattern_dialog_name)
            )
            EditTextInDialog(
                value = pattern.brand,
                onValueChange = viewModel::onBrandChange,
                label = stringResource(id = R.string.add_pattern_dialog_brand)
            )
            EditTextInDialog(
                value = pattern.serial,
                onValueChange = viewModel::onSerialChange,
                label = stringResource(id = R.string.add_pattern_dialog_serial)
            )
            ExposedDropdownMenuBox(
                expanded = uiState.expandedCertificates,
                onExpandedChange = {viewModel.uiState.value = uiState.copy(expandedCertificates = !uiState.expandedCertificates)
                })
            {
                OutlinedTextField(
                    value = pattern.certificate.name,
                    label = { Text(text = stringResource(id = R.string.add_pattern_dialog_certificate)) },
                    onValueChange = {  },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedCertificates) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = uiState.expandedCertificates,
                    onDismissRequest = { viewModel.uiState.value = uiState.copy(expandedCertificates = false)}
                ) {
                    certificates?.forEachIndexed { index, certificate ->
                        DropdownMenuItem(
                            text = {
                                Column{
                                    Text(text = certificate.name)
                                    Text(text = certificate.certificateId)
                                    HorizontalDivider()
                                }
                            },
                            onClick = {
                                if (index >=0){
                                    viewModel.onCertificateChange(certificate)
                                    viewModel.uiState.value = uiState.copy(expandedCertificates = false)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}