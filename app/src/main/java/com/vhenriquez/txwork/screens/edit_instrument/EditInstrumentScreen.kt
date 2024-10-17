package com.vhenriquez.txwork.screens.edit_instrument

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.AddItemDialog
import com.vhenriquez.txwork.common.composable.DropdownSelector
import com.vhenriquez.txwork.common.composable.EditTextInDialog
import com.vhenriquez.txwork.common.composable.PopupBox
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.InstrumentEntity

@Composable
fun EditInstrumentScreen(
    popUp: () -> Unit,
    viewModel: EditInstrumentViewModel = hiltViewModel(),) {

    EditInstrumentScreenContent(
        viewModel = viewModel,
        popUp = popUp
    )
}

@Composable
fun EditInstrumentScreenContent(
    viewModel: EditInstrumentViewModel,
    popUp: () -> Unit
    ) {
    val instrumentEntity by viewModel.instrument
    val business by viewModel.business.collectAsState()
    val uiState by viewModel.uiState
    PopupBox(
        title = if (instrumentEntity.id.isEmpty()) stringResource(id = R.string.title_add_instrument)
        else stringResource(id = R.string.edit_instrument_dialog_title),
        isFullScreen = !booleanResource(id = R.bool.large_layout),
        onDismiss = popUp,
        onConfirm = { viewModel.onSaveData(popUp) })
    {
        when (instrumentEntity.magnitude) {
            stringArrayResource(id = R.array.magnitude_options)[0] -> {
            }
            stringArrayResource(id = R.array.magnitude_options)[0],
            stringArrayResource(id = R.array.magnitude_options)[2],
            stringArrayResource(id = R.array.magnitude_options)[10] -> {
                viewModel.uiState.value = uiState.copy(unitOptionsPv = stringArrayResource(id = R.array.pressure_unit_options).asList())
            }
            stringArrayResource(id = R.array.magnitude_options)[1] -> {
                viewModel.uiState.value = uiState.copy(unitOptionsPv = stringArrayResource(id = R.array.temperature_unit_options).asList())
            }
            stringArrayResource(id = R.array.magnitude_options)[3],
            stringArrayResource(id = R.array.magnitude_options)[4] -> {
                viewModel.uiState.value = uiState.copy(unitOptionsPv = stringArrayResource(id = R.array.flow_unit_options).asList())
            }
            stringArrayResource(id = R.array.magnitude_options)[5],
            stringArrayResource(id = R.array.magnitude_options)[7],
            stringArrayResource(id = R.array.magnitude_options)[8],
            stringArrayResource(id = R.array.magnitude_options)[9],
            stringArrayResource(id = R.array.magnitude_options)[11],
            stringArrayResource(id = R.array.magnitude_options)[12],
            stringArrayResource(id = R.array.magnitude_options)[13] -> {
                viewModel.uiState.value = uiState.copy(unitOptionsPv = stringArrayResource(id = R.array.analytics_unit_options).asList())
            }
            stringArrayResource(id = R.array.magnitude_options)[6] -> {
                viewModel.uiState.value = uiState.copy(unitOptionsPv = stringArrayResource(id = R.array.level_unit_options).asList())
            }
        }
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Text("Informacion General", modifier = Modifier.padding(start = 10.dp))
            Card {
                Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)){
                    EditTextInDialog(
                        value = instrumentEntity.tag,
                        onValueChange = viewModel::onTagChange,
                        label = "TAG"
                    )
                    //**********************************BUSINESS****************************************************
                    DropdownSelector(
                        label = R.string.add_instrument_dialog_business,
                        options = business.map { it.name },
                        selection = instrumentEntity.business,
                        modifier = Modifier
                    ) { _, index ->
                        viewModel.onBusinessChange(business[index].name, business[index].id)
                    }
                    EditTextInDialog(
                        value = instrumentEntity.description,
                        onValueChange = viewModel::onDescriptionChange,
                        stringResource(id = R.string.add_instrument_dialog_description)
                    )
                    EditTextInDialog(
                        value = instrumentEntity.area,
                        onValueChange = viewModel::onAreaChange,
                        stringResource(id = R.string.add_instrument_dialog_area)
                    )
                    //**********************************INSTRUMENT TYPE****************************************************
                    DropdownSelector(
                        label = R.string.add_instrument_dialog_instrumentType,
                        options = stringArrayResource(id = R.array.instrument_type_options).asList(),
                        selection = instrumentEntity.instrumentType,
                        modifier = Modifier
                    ) { item, position ->
                        when (position) {
                            3, 4 -> {
                                viewModel.uiState.value = uiState.copy(isVisibilityMagnitude =false)
                                viewModel.onMagnitudeChange(if (position == 3) "Presión" else "Temperatura")
                            }

                            else -> {
                                viewModel.uiState.value = uiState.copy(isVisibilityMagnitude =true)
                            }
                        }
                        viewModel.onInstrumentTypeChange(item)
                    }
                    //**********************************MAGNITUDE****************************************************
                    if (uiState.isVisibilityMagnitude) {
                        DropdownSelector(
                            label = R.string.add_instrument_dialog_magnitude,
                            options = stringArrayResource(id = R.array.magnitude_options).asList(),
                            selection = instrumentEntity.magnitude,
                            modifier = Modifier
                        ) { item, _ ->
                            viewModel.onMagnitudeChange(item)
                        }
                    }
                }
            }

            if (uiState.setRange) {
                Text("Rango verificación PV", modifier = Modifier.padding(start = 10.dp))
                Card {
                    Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)){
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        EditTextInDialog(
                            value = instrumentEntity.verificationMin,
                            onValueChange = viewModel::onVerificationMinChange,
                            label = "LRV",
                            modifier = Modifier.weight(4f),
                            keyboardType = KeyboardType.Number
                        )
                        Text(
                            text = "@",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center
                        )
                        EditTextInDialog(
                            value = instrumentEntity.verificationMax,
                            onValueChange = viewModel::onVerificationMaxChange,
                            label = "URV",
                            modifier = Modifier.weight(4f),
                            keyboardType = KeyboardType.Number
                        )
                    }
                    //**********************************VERIFICATION UNIT****************************************************
                    DropdownSelector(
                        label = R.string.add_instrument_dialog_verificationUnit,
                        options = uiState.unitOptionsPv,
                        selection = instrumentEntity.verificationUnit,
                        modifier = Modifier
                    ) { item, index ->
                        if (index == uiState.unitOptionsPv.lastIndex){
                            viewModel.uiState.value = uiState.copy(showDialogNewVerificationUnit = true)
                        }else
                            viewModel.onVerificationUnitChange(item)
                    }
                    }
                }

                if (instrumentEntity.magnitude == stringArrayResource(id = R.array.magnitude_options)[4]) {
                    Text("Rango verificación SV", modifier = Modifier.padding(start = 10.dp))
                    Card {
                        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                EditTextInDialog(
                                    value = instrumentEntity.verificationMinSV,
                                    onValueChange = viewModel::onVerificationMinSVChange,
                                    label = "LRV",
                                    modifier = Modifier.weight(4f),
                                    keyboardType = KeyboardType.Number
                                )
                                Text(
                                    text = "@",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Center
                                )
                                EditTextInDialog(
                                    value = instrumentEntity.verificationMaxSV,
                                    onValueChange = { viewModel.onVerificationMaxSVChange(it) },
                                    label = "URV",
                                    modifier = Modifier.weight(4f),
                                    keyboardType = KeyboardType.Number
                                )
                            }
                            //**********************************VERIFICATION UNIT****************************************************
                            DropdownSelector(
                                label = R.string.add_instrument_dialog_verificationUnit,
                                options = stringArrayResource(R.array.pressure_unit_options).asList(),
                                selection = instrumentEntity.verificationUnitSV,
                                modifier = Modifier
                            ) { item, _ ->
                                viewModel.onVerificationUnitSVChange(item)
                            }
                        }
                    } // SV
                    Text("Rango verificación TV", modifier = Modifier.padding(start = 10.dp))
                    Card {
                        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                EditTextInDialog(
                                    value = instrumentEntity.verificationMinTV,
                                    onValueChange = viewModel::onVerificationMinTVChange,
                                    label = "LRV",
                                    modifier = Modifier.weight(4f),
                                    keyboardType = KeyboardType.Number
                                )
                                Text(
                                    text = "@",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Center
                                )
                                EditTextInDialog(
                                    value = instrumentEntity.verificationMaxTV,
                                    onValueChange = viewModel::onVerificationMaxTVChange,
                                    label = "URV",
                                    modifier = Modifier.weight(4f),
                                    keyboardType = KeyboardType.Number
                                )
                            }
                            //**********************************VERIFICATION UNIT****************************************************
                            DropdownSelector(
                                label = R.string.add_instrument_dialog_verificationUnit,
                                options = stringArrayResource(R.array.pressure_unit_options).asList(),
                                selection = instrumentEntity.verificationUnitTV,
                                modifier = Modifier
                            ) { item, _ ->
                                viewModel.onVerificationUnitTVChange(item)
                            }
                        }
                    } // TV
                    Text("Rango verificación QV", modifier = Modifier.padding(start = 10.dp))
                    Card {
                        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                EditTextInDialog(
                                    value = instrumentEntity.verificationMinQV,
                                    onValueChange = viewModel::onVerificationMinQVChange,
                                    label = "LRV",
                                    modifier = Modifier.weight(4f),
                                    keyboardType = KeyboardType.Number
                                )
                                Text(
                                    text = "@",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Center
                                )
                                EditTextInDialog(
                                    value = instrumentEntity.verificationMaxQV,
                                    onValueChange = viewModel::onVerificationMaxQVChange,
                                    label = "URV",
                                    modifier = Modifier.weight(4f),
                                    keyboardType = KeyboardType.Number
                                )
                            }
                            //**********************************VERIFICATION UNIT****************************************************
                            DropdownSelector(
                                label = R.string.add_instrument_dialog_verificationUnit,
                                options = stringArrayResource(R.array.temperature_unit_options).asList(),
                                selection = instrumentEntity.verificationUnitQV,
                                modifier = Modifier
                            ) { item, _ ->
                                viewModel.onVerificationUnitQVChange(item)
                            }
                        }
                    } // QV
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Checkbox(checked = uiState.setRange, onCheckedChange = {viewModel.uiState.value = uiState.copy(setRange = it)})
                Text(stringResource(id = R.string.add_instrument_dialog_setRange))
            }
            //**********************************REPORT TYPE****************************************************
            Text("Información reporte", modifier = Modifier.padding(start = 10.dp))
            Card {
                Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
                    DropdownSelector(
                        label = R.string.add_instrument_dialog_reportType,
                        options = stringArrayResource(id = R.array.report_type_options).asList(),
                        selection = instrumentEntity.reportType,
                        modifier = Modifier
                    ) { item, _ ->
                        viewModel.onReportTypeChange(item)
                    }
                }
            }
        }
    }

    AnimatedVisibility(uiState.showDialogNewVerificationUnit) {
        AddItemDialog(
            title = stringResource(id = R.string.add_certificate_dialog_new_verificationUnit),
            value = "",
            onDismiss = { viewModel.uiState.value = uiState.copy(showDialogNewVerificationUnit =false) },
            onConfirm = viewModel::onVerificationUnitChange
        )
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    val viewModel : EditInstrumentViewModel = hiltViewModel()
    EditInstrumentScreenContent (
        viewModel = viewModel,
        popUp = {}
    )
}



