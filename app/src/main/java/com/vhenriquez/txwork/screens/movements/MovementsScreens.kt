package com.vhenriquez.txwork.screens.movements

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.DropdownSelector
import com.vhenriquez.txwork.common.composable.EditTextCalibration
import com.vhenriquez.txwork.common.composable.EditTextInDialog
import com.vhenriquez.txwork.common.composable.PopupBox
import com.vhenriquez.txwork.common.composable.SearchTextField
import com.vhenriquez.txwork.common.composable.TextSpannable
import com.vhenriquez.txwork.model.CalibrationEntity
import com.vhenriquez.txwork.model.ImageEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.PatternEntity
import com.vhenriquez.txwork.navigation.Main
import com.vhenriquez.txwork.screens.activities.ActivityItem
import com.vhenriquez.txwork.utils.CommonUtils.getColor

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Information(
    viewModel: DetailInstrumentViewModel) {

    ContentInformation(
        viewModel = viewModel,
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ContentInformation(
    viewModel: DetailInstrumentViewModel) {
    val uiState by viewModel.uiState
    val instrument by viewModel.selectedInstrument
    val images by viewModel.images
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){
        Text(
            text = stringResource(id = R.string.info_title),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp),
            style = MaterialTheme.typography.titleMedium,
        )
        Column(
            Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp, 4.dp, 8.dp, 4.dp)
                .clickable {
                    viewModel.uiState.value =
                        viewModel.uiState.value.copy(showDialogEditInfo = true)
                }){

            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier= Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                TextSpannable(textSpannable = R.string.info_brand, text = instrument.brand, modifier = Modifier.weight(1f))
                TextSpannable(textSpannable = R.string.info_model, text = instrument.model, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier= Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                TextSpannable(textSpannable = R.string.info_serial, text = instrument.serial, modifier = Modifier.weight(1f))
                TextSpannable(textSpannable = R.string.info_damping, text = instrument.damping, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextSpannable(textSpannable = R.string.info_instrument_range, text = instrument.getRangeInstrument(), modifier = Modifier)
            Spacer(modifier = Modifier.height(8.dp))
            TextSpannable(textSpannable = R.string.info_verification_range_pv, text = instrument.getRangeVerification(), modifier = Modifier)
            Spacer(modifier = Modifier.height(8.dp))
            if (instrument.magnitude == stringArrayResource(id = R.array.magnitude_options)[4]){
                TextSpannable(textSpannable = R.string.info_verification_range_sv, text = instrument.getRangeVerificationSV(), modifier = Modifier)
                Spacer(modifier = Modifier.height(8.dp))
                TextSpannable(textSpannable = R.string.info_verification_range_tv, text = instrument.getRangeVerificationTV(), modifier = Modifier)
                Spacer(modifier = Modifier.height(8.dp))
                TextSpannable(textSpannable = R.string.info_verification_range_qv, text = instrument.getRangeVerificationQV(), modifier = Modifier)
                Spacer(modifier = Modifier.height(8.dp))
            }
            TextSpannable(textSpannable = R.string.info_output, text = instrument.output, modifier = Modifier)
            Spacer(modifier = Modifier.height(8.dp))
            TextSpannable(textSpannable = R.string.info_sensor_type, text = instrument.sensorType, modifier = Modifier)
            Spacer(modifier = Modifier.height(4.dp))

            if (instrument.instrumentType == stringArrayResource(id = R.array.instrument_type_options)[3] ||
                instrument.instrumentType == stringArrayResource(id = R.array.instrument_type_options)[4]){
                Row(modifier= Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    TextSpannable(textSpannable = R.string.info_diameter, text = instrument.diameter, modifier = Modifier.weight(1f))
                    TextSpannable(textSpannable = R.string.info_resolution, text = instrument.resolution, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextSpannable(textSpannable = R.string.info_process_connection, text = instrument.processConnection, modifier = Modifier)
                Spacer(modifier = Modifier.height(8.dp))
                if (instrument.instrumentType == stringArrayResource(id = R.array.instrument_type_options)[4]){
                    Row(modifier= Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        TextSpannable(textSpannable = R.string.info_sensor_diameter, text = instrument.sensorDiameter, modifier = Modifier.weight(1f))
                        TextSpannable(textSpannable = R.string.info_useful_length, text = instrument.usefulLength, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        Text(text =stringResource(id = R.string.info_observations_title),
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(4.dp),
            style = MaterialTheme.typography.titleMedium,)
        Column(
            Modifier
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
                .padding(8.dp, 4.dp, 8.dp, 4.dp)
                .clickable {
                    viewModel.uiState.value =
                        viewModel.uiState.value.copy(showDialogObservationsInstrument = true)
                }){
            Text(text = instrument.observations,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp))
        }
        Spacer(Modifier.height(16.dp))
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(1),
            modifier = Modifier
                .height(200.dp)
                .width(200.dp)
                .align(Alignment.Start)
                .padding(start = 16.dp, top = 16.dp)) {
            items(images, key = { it.id }) { image ->
                Box(modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)) {
                    AsyncImage(
                        model = image.photoUrl,  // URL de la imagen
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = {},
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            Icons.Outlined.ArrowCircleDown,
                            contentDescription = "deleteIcon",
                            modifier = Modifier.background(Color.Transparent),
                            tint = Color.White
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(color = Color.Black.copy(alpha = 0.5f)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(image.name)
                        IconButton(
                            onClick = {viewModel.deleteImage(image)},
                            modifier = Modifier
                        ) {
                            Icon(
                                if (image.photoUrl.contains("https://"))
                                    Icons.Outlined.Delete
                                else
                                    Icons.Outlined.Download,
                                contentDescription = "deleteIcon",
                                modifier = Modifier.background(Color.Transparent),
                                tint = Color.White
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    AnimatedVisibility(visible = uiState.showDialogObservationsInstrument) {
        ObservationsDialog(
            observations = instrument.observations,
            onObservationsChange = viewModel::onObservationsInstrumentChange,
            onDismiss = {viewModel.uiState.value = viewModel.uiState.value.copy(showDialogObservationsInstrument = false)},
            onConfirm = {viewModel.saveInstrumentUpdate()})
    }

    AnimatedVisibility(visible = uiState.showDialogEditInfo) {
        EditInfoDialog(
            instrument = instrument,
            viewModel = viewModel)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditInfoDialog(
    instrument: InstrumentEntity,
    viewModel: DetailInstrumentViewModel) {

    if (booleanResource(id = R.bool.large_layout)){
        AlertDialog(
            onDismissRequest = {viewModel.uiState.value = viewModel.uiState.value.copy(showDialogEditInfo = false)},
            title = { Text(text = stringResource(id = R.string.title_add_instrument)) },
            confirmButton = {
                Button(onClick = {viewModel.saveInstrumentUpdate()}
                ){
                    Text(text = "OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        viewModel.uiState.value = viewModel.uiState.value.copy(showDialogEditInfo = false)
                    }
                ) {
                    Text(text = "Cancelar")
                }
            },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    ContentDialogInfo(
                        instrument = instrument,
                        viewModel = viewModel)
                }
            })
    }else{
        Dialog(
            onDismissRequest = {},
            properties =DialogProperties(usePlatformDefaultWidth = false),
            {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(id = R.string.dialog_edit_info_title)) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    viewModel.uiState.value = viewModel.uiState.value.copy(showDialogEditInfo = false)
                                }) {
                                    Icon(imageVector = Icons.Default.Close,
                                        contentDescription = "Close")
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    viewModel.saveInstrumentUpdate()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }

                        )
                    },
                    content = {innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            ContentDialogInfo(
                                instrument = instrument,
                                viewModel = viewModel)
                        }
                    }
                )
            }
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ContentDialogInfo(
    instrument: InstrumentEntity,
    viewModel: DetailInstrumentViewModel
){
    var unitOptions by remember { mutableStateOf(listOf<String>()) }
    var sensorTypeOptions by remember { mutableStateOf(listOf<String>()) }

    when(instrument.magnitude){
        stringArrayResource(id = R.array.magnitude_options)[0] -> {
        }
        stringArrayResource(id = R.array.magnitude_options)[0],
        stringArrayResource(id = R.array.magnitude_options)[2],
        stringArrayResource(id = R.array.magnitude_options)[10]-> {
            unitOptions = stringArrayResource(id = R.array.pressure_unit_options).asList()
            sensorTypeOptions = stringArrayResource(id = R.array.sensorTypePressure).asList()
        }
        stringArrayResource(id = R.array.magnitude_options)[1]-> {
            unitOptions = stringArrayResource(id = R.array.temperature_unit_options).asList()
            sensorTypeOptions = stringArrayResource(id = R.array.sensorTypeTemperature).asList()
        }
        stringArrayResource(id = R.array.magnitude_options)[3],
        stringArrayResource(id = R.array.magnitude_options)[4]-> {
            unitOptions = stringArrayResource(id = R.array.flow_unit_options).asList()
            sensorTypeOptions = stringArrayResource(id = R.array.sensorTypeFlow).asList()
        }
        stringArrayResource(id = R.array.magnitude_options)[5],
        stringArrayResource(id = R.array.magnitude_options)[7],
        stringArrayResource(id = R.array.magnitude_options)[8],
        stringArrayResource(id = R.array.magnitude_options)[9],
        stringArrayResource(id = R.array.magnitude_options)[11],
        stringArrayResource(id = R.array.magnitude_options)[12],
        stringArrayResource(id = R.array.magnitude_options)[13] -> {
            unitOptions = stringArrayResource(id = R.array.analytics_unit_options).asList()
            sensorTypeOptions = stringArrayResource(id = R.array.sensorTypeAnalytics).asList()
        }
        stringArrayResource(id = R.array.magnitude_options)[6] -> {
            unitOptions = stringArrayResource(id = R.array.level_unit_options).asList()
            sensorTypeOptions = stringArrayResource(id = R.array.sensorTypeLevel).asList()
        }
    }
    Text(stringResource(R.string.dialog_edit_info_title_transmitterInfo), modifier = Modifier.padding(start = 10.dp))
    Card {
        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
            EditTextInDialog(
                value = instrument.brand,
                onValueChange = viewModel::onBrandChange,
                stringResource(R.string.dialog_edit_info_brand)
            )
            EditTextInDialog(
                value = instrument.model,
                onValueChange = viewModel::onModelChange,
                stringResource(R.string.dialog_edit_info_model)
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                EditTextInDialog(
                    value = instrument.serial,
                    onValueChange = viewModel::onSerialChange,
                    label = stringResource(R.string.dialog_edit_info_serial),
                    modifier = Modifier.weight(4f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                EditTextInDialog(
                    value = instrument.damping,
                    onValueChange = viewModel::onDampingChange,
                    label = stringResource(R.string.dialog_edit_info_damping),
                    modifier = Modifier.weight(2f),
                    keyboardType = KeyboardType.Number
                )
            }
        }
    }
    Text(stringResource(R.string.dialog_edit_info_title_range), modifier = Modifier.padding(start = 10.dp))
    Card {
        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                EditTextInDialog(
                    value = instrument.measurementMin,
                    onValueChange = viewModel::onMeasurementMinChange,
                    label = "LRV",
                    modifier = Modifier.weight(4f),
                    keyboardType = KeyboardType.Number
                )
                Text(text = "@",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
                EditTextInDialog(
                    value = instrument.measurementMax,
                    onValueChange = viewModel::onMeasurementMaxChange,
                    label = "URV",
                    modifier = Modifier.weight(4f),
                    keyboardType = KeyboardType.Number
                )
            }
            //**********************************VERIFICATION UNIT****************************************************
            DropdownSelector(
                label = R.string.dialog_edit_info_unit,
                options = unitOptions,
                selection = instrument.measurementUnit,
                modifier = Modifier
            ) {item, _-> viewModel.onMeasurementUnitChange(item)}
        }
    }
    Text(stringResource(R.string.dialog_edit_info_title_verification_pv), modifier = Modifier.padding(start = 10.dp))
    Card {
        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                EditTextInDialog(
                    value = instrument.verificationMin,
                    onValueChange = viewModel::onVerificationMinChange,
                    label = "LRV",
                    modifier = Modifier.weight(4f),
                    keyboardType = KeyboardType.Number
                )
                Text(text = "@",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
                EditTextInDialog(
                    value = instrument.verificationMax,
                    onValueChange = viewModel::onVerificationMaxChange,
                    label = "URV",
                    modifier = Modifier.weight(4f),
                    keyboardType = KeyboardType.Number
                )
            }
            //**********************************VERIFICATION UNIT PV****************************************************
            DropdownSelector(
                label = R.string.dialog_edit_info_unit,
                options = unitOptions,
                selection = instrument.verificationUnit,
                modifier = Modifier
            ) {item, _-> viewModel.onVerificationUnitChange(item)}
        }
    }
    if (instrument.magnitude == stringArrayResource(id = R.array.magnitude_options)[4]) {
        Text(stringResource(R.string.dialog_edit_info_title_verification_sv), modifier = Modifier.padding(start = 10.dp))
        Card {
            Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    EditTextInDialog(
                        value = instrument.verificationMinSV,
                        onValueChange = viewModel::onVerificationMinSVChange,
                        label = "LRV",
                        modifier = Modifier.weight(4f),
                        keyboardType = KeyboardType.Number
                    )
                    Text(text = "@",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center)
                    EditTextInDialog(
                        value = instrument.verificationMaxSV,
                        onValueChange = viewModel::onVerificationMaxSVChange,
                        label = "URV",
                        modifier = Modifier.weight(4f),
                        keyboardType = KeyboardType.Number
                    )
                }
                //**********************************VERIFICATION UNIT SV****************************************************
                DropdownSelector(
                    label = R.string.dialog_edit_info_unit,
                    options = stringArrayResource(R.array.pressure_unit_options).asList(),
                    selection = instrument.verificationUnitSV,
                    modifier = Modifier
                ) {item, _-> viewModel.onVerificationUnitSVChange(item)}
            }
        }
        Text(stringResource(R.string.dialog_edit_info_title_verification_tv), modifier = Modifier.padding(start = 10.dp))
        Card {
            Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    EditTextInDialog(
                        value = instrument.verificationMinTV,
                        onValueChange = viewModel::onVerificationMinTVChange,
                        label = "LRV",
                        modifier = Modifier.weight(4f),
                        keyboardType = KeyboardType.Number
                    )
                    Text(text = "@",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center)
                    EditTextInDialog(
                        value = instrument.verificationMaxTV,
                        onValueChange = viewModel::onVerificationMaxTVChange,
                        label = "URV",
                        modifier = Modifier.weight(4f),
                        keyboardType = KeyboardType.Number
                    )
                }
                //**********************************VERIFICATION UNIT TV****************************************************
                DropdownSelector(
                    label = R.string.dialog_edit_info_unit,
                    options = stringArrayResource(R.array.pressure_unit_options).asList(),
                    selection = instrument.verificationUnitTV,
                    modifier = Modifier
                ) {item, _-> viewModel.onVerificationUnitTVChange(item)}
            }
        }
        Text(stringResource(R.string.dialog_edit_info_title_verification_qv), modifier = Modifier.padding(start = 10.dp))
        Card {
            Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    EditTextInDialog(
                        value = instrument.verificationMinQV,
                        onValueChange = viewModel::onVerificationMinQVChange,
                        label = "LRV",
                        modifier = Modifier.weight(4f),
                        keyboardType = KeyboardType.Number
                    )
                    Text(text = "@",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center)
                    EditTextInDialog(
                        value = instrument.verificationMaxQV,
                        onValueChange = viewModel::onVerificationMaxQVChange,
                        label = "URV",
                        modifier = Modifier.weight(4f),
                        keyboardType = KeyboardType.Number
                    )
                }
                //**********************************VERIFICATION UNIT QV****************************************************
                DropdownSelector(
                    label = R.string.dialog_edit_info_unit,
                    options = stringArrayResource(R.array.temperature_unit_options).asList(),
                    selection = instrument.verificationUnitQV,
                    modifier = Modifier
                ) {item, _-> viewModel.onVerificationUnitQVChange(item)}
            }
        }
    }
    //**********************************OUTPUT****************************************************
    Text(stringResource(R.string.dialog_edit_info_title_output), modifier = Modifier.padding(start = 10.dp))
    Card {
        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
            DropdownSelector(
                label = R.string.dialog_edit_info_output,
                options = stringArrayResource(id = R.array.transmitterOutput).asList(),
                selection = instrument.output,
                modifier = Modifier
            ) {item, _-> viewModel.onOutputChange(item)}
        }
    }
    //**********************************SENSOR****************************************************
    Text(stringResource(R.string.dialog_edit_info_title_sensor), modifier = Modifier.padding(start = 10.dp))
    Card {
        Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
            DropdownSelector(
                label = R.string.dialog_edit_info_sensor_type,
                options = sensorTypeOptions,
                selection = instrument.sensorType,
                modifier = Modifier
            ) {item, _-> viewModel.onSensorTypeChange(item)}
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Calibration(viewModel: DetailInstrumentViewModel) {

    ContentCalibration(
        viewModel = viewModel,
        localFocusManager = LocalFocusManager.current,
        keyboardController = LocalSoftwareKeyboardController.current)
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ContentCalibration(
    viewModel: DetailInstrumentViewModel,
    keyboardController: SoftwareKeyboardController?,
    localFocusManager: FocusManager){
    val uiState by viewModel.uiState
    val selectedInstrument by viewModel.selectedInstrument
    val calibrationInfo by viewModel.calibrationInfo
    val calibrationValues by viewModel.calibrationValues
    val patterns by viewModel.patterns
    val reportOptions = stringArrayResource(id = R.array.report_type_options)
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){
        Text(
            text = stringResource(id = R.string.calibration_title_patterns),
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp),
            style = MaterialTheme.typography.titleMedium,
        )
        Column(
            Modifier
                .defaultMinSize(minHeight = 60.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
                .padding(8.dp, 4.dp, 8.dp, 4.dp)
                .clickable { viewModel.uiState.value = uiState.copy(showDialogPatterns = true) }){
            LazyColumn(Modifier.fillMaxWidth()) {
                calibrationInfo.patternEntities.forEach {
                    item {
                        Text(text = it.name, Modifier.padding(2.dp))
                        HorizontalDivider()
                    }
                }
            }
        }

        Text(
            text = stringResource(id = R.string.calibration_title_service),
            Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleMedium,
        )
        Column(
            Modifier
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(8.dp, 4.dp, 8.dp, 4.dp)){
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = calibrationInfo.service,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            viewModel.uiState.value = uiState.copy(showDialogService = true)
                        },
                    textAlign = TextAlign.Center)
                VerticalDivider(
                    Modifier
                        .height(35.dp)
                        .align(Alignment.CenterVertically))
                Text(text = calibrationInfo.calibrationType,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            viewModel.uiState.value = uiState.copy(showDialogCalibrationType = true)
                        },
                    textAlign = TextAlign.Center)
            }
        }

        Text(
            text = stringResource(id = R.string.calibration_title_calibration),
            Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleMedium,
        )
        Column(
            Modifier
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(8.dp, 4.dp, 8.dp, 4.dp)
                .clickable {viewModel.uiState.value = uiState.copy(showDialogCalibration = true)}){

            val table = mutableListOf<Map<String,Any>>()

            val calibrationsValues = calibrationInfo.calibrationValues

            when(selectedInstrument.reportType){
                stringArrayResource(R.array.report_type_options)[5]->{//Switch
                    table.add(mapOf(
                        "date" to stringArrayResource(R.array.header1_switch),
                        "type" to 0))
                    val datesCol = (calibrationsValues?.getOrDefault("values 0", emptyMap()) ?: emptyMap())

                    table.add(mapOf(
                        "date" to arrayOf(
                            selectedInstrument.getRangeVerification(),
                            datesCol.getOrDefault("value 4", ""),
                            datesCol.getOrDefault("value 5", "")),
                        "type" to 1))

                    table.add(mapOf(
                        "date" to stringArrayResource(R.array.header2_switch),
                        "type" to 0))
                    table.add(mapOf(
                        "date" to stringArrayResource(R.array.header3_switch),
                        "type" to 0))
                    table.add(mapOf(
                        "date" to arrayOf(
                            datesCol.getOrDefault("value 0", ""),
                            datesCol.getOrDefault("value 1", ""),
                            datesCol.getOrDefault("value 2", ""),
                            datesCol.getOrDefault("value 3", "")
                        ),
                        "type" to 1))
                }
                stringArrayResource(R.array.report_type_options)[6]-> {//ZR
                    table.add(
                        mapOf(
                            "date" to arrayOf(
                                "Patron\n[${selectedInstrument.verificationUnit}]",
                                "Valor\nEncontrado", "Valor\nDejado"
                            ),
                            "type" to 0
                        )
                    )

                    for (row in 0..1) {
                        val datesCol =
                            calibrationsValues?.getOrDefault("values $row", emptyMap()) ?: emptyMap()
                        table.add(
                            mapOf(
                                "date" to arrayOf(
                                    datesCol.getOrDefault("value 0", ""),
                                    datesCol.getOrDefault("value 1", ""),
                                    datesCol.getOrDefault("value 2", "")
                                ),
                                "type" to 1
                            )
                        )
                    }
                    table.add(
                        mapOf(
                            "date" to arrayOf(
                                "Item", "Antes", "Después", "Estado"
                            ),
                            "type" to 0
                        )
                    )

                    for (row in 2..11) {
                        val datesCol =
                            calibrationsValues?.getOrDefault("values $row", emptyMap()) ?: emptyMap()
                        table.add(
                            mapOf(
                                "date" to arrayOf(
                                    datesCol.getOrDefault("value 0", ""),
                                    datesCol.getOrDefault("value 1", ""),
                                    datesCol.getOrDefault("value 2", ""),
                                    datesCol.getOrDefault("value 3", "")
                                ),
                                "type" to 1
                            )
                        )
                    }
                }
                else->{
                    val isMV = selectedInstrument.magnitude == "Flujo Multivariable"
                    table.add(
                        mapOf(
                            "date" to arrayOf(
                                "Patron\n[${if (isMV) selectedInstrument.verificationUnitSV
                                else selectedInstrument.verificationUnit}]",
                                "Valor\nEncontrado", "Valor\nDejado"
                            ),
                            "type" to 0
                        )
                    )
                    val rows = when (selectedInstrument.reportType) {
                        stringArrayResource(R.array.report_type_options)[3],
                        stringArrayResource(R.array.report_type_options)[4] -> 2   ///////   "Portatil", "PH" -> 2
                        stringArrayResource(R.array.report_type_options)[0] -> 4   //////////"Sensor" -> 4
                        else -> 28
                    }

                    for (row in 0 ..rows){
                        if ((row == 9 || row == 19) && isMV ){
                            table.add(
                                mapOf(
                                    "date" to arrayOf(
                                        "Patron\n[${if (row == 9) selectedInstrument.verificationUnitTV
                                        else selectedInstrument.verificationUnitQV}]",
                                        "Valor\nEncontrado", "Valor\nDejado"),
                                    "type" to 0
                                )
                            )
                            continue
                        }
                        if (row > 8 && !isMV)
                            continue

                        val datesCol =
                            calibrationsValues?.getOrDefault("values $row", emptyMap()) ?: emptyMap()
                        table.add(
                            mapOf(
                                "date" to arrayOf(
                                    datesCol.getOrDefault("value 0",""),
                                    datesCol.getOrDefault("value 1",""),
                                    datesCol.getOrDefault("value 2","")),
                                "type" to 1
                            )
                        )

                    }
                }

            }

            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(1.dp)) {
                table.forEach {row->
                    when(row["type"]){
                        0->{
                            val data = row["date"] as Array<*>
                            item {
                                Row(Modifier.background(Color.Cyan)) {
                                    data.forEach {title->
                                        TableCell(text = title.toString(), weight = 1f)
                                    }
                                }
                            }
                        }
                        1->{
                            val date = row["date"] as Array<*>
                            item {
                                Row(
                                    Modifier
                                        .background(Color.White)
                                        .fillMaxWidth()) {
                                    date.forEach {title->
                                        TableCell(text = title.toString(), weight = 1f)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        Text(text =stringResource(id = R.string.info_observations_calibration_title),
            Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleMedium,)
        Column(
            Modifier
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
                .padding(8.dp, 4.dp, 8.dp, 4.dp)
                .clickable {
                    viewModel.uiState.value = uiState.copy(showDialogObservationsCalibration = true)
                }){
            Text(text = calibrationInfo.observation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp))
        }
    }

    AnimatedVisibility(visible = uiState.showDialogObservationsCalibration) {
        ObservationsDialog(
            observations = calibrationInfo.observation,
            onObservationsChange = viewModel::onObservationsCalibrationChange,
            onDismiss = {viewModel.uiState.value = uiState.copy(showDialogObservationsCalibration = false)},
            onConfirm = {viewModel.saveCalibrationInfoUpdate()})
    }

    AnimatedVisibility(visible = uiState.showDialogPatterns) {
        MultiChoiceConfirmationDialog(
            patterns = patterns,
            calibrationPatterns = calibrationInfo.patternEntities,
            titleText = "Seleccione Patrones Utilizados",
            onConfirm = { selectedPatterns: List<PatternEntity> ->
                viewModel.setActions(selectedPatterns)
            },
            onDismiss = { viewModel.uiState.value = uiState.copy(showDialogPatterns = false) }
        )
    }

    AnimatedVisibility(visible = uiState.showDialogService) {
        val options = stringArrayResource(R.array.service)
        AlertDialog(
            onDismissRequest = {viewModel.uiState.value = uiState.copy(showDialogService = false)},
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.uiState.value = uiState.copy(showDialogService = false)
                    }
                ) {
                    Text("Cancelar")
                }
            },
            confirmButton = {},
            title = { Text(
                text = stringResource(id = R.string.dialog_service_title),
                style = MaterialTheme.typography.titleMedium
            ) },
            text = {
                LazyColumn(Modifier.fillMaxWidth()) {
                    options.forEach {option ->
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onServiceChange(option) }
                            ){
                                Text(
                                    text = option,
                                    modifier = Modifier.padding(10.dp))
                            }
                            HorizontalDivider()

                        }
                    }
                }

            })

    }

    AnimatedVisibility(visible = uiState.showDialogCalibrationType) {
        val options = stringArrayResource(R.array.calibrationType)
        AlertDialog(
            onDismissRequest = {viewModel.uiState.value = uiState.copy(showDialogCalibrationType = false)},
            dismissButton = {
                TextButton(
                    onClick = {viewModel.uiState.value = uiState.copy(showDialogCalibrationType = false)}
                ) {
                    Text("Cancelar")
                }
            },
            confirmButton = {},
            title = { Text(
                text = stringResource(id = R.string.dialog_calibrationType_title),
                style = MaterialTheme.typography.titleMedium
            ) },
            text = {
                LazyColumn(Modifier.fillMaxWidth()) {
                    options.forEach {option ->
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onCalibrationTypeChange(option) }
                            ){
                                Text(
                                    text = option,
                                    modifier = Modifier.padding(10.dp))
                            }
                            HorizontalDivider()
                        }
                    }
                }

            })
    }

    AnimatedVisibility(visible = uiState.showDialogCalibration) {
        PopupBox(
            title = stringResource(R.string.dialog_edit_calibration_title),
            isFullScreen = !booleanResource(R.bool.large_layout),
            onDismiss = {viewModel.uiState.value = uiState.copy(showDialogCalibration = false) },
            onConfirm = {viewModel.saveCalibrationDates()}) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(text = "${if (selectedInstrument.instrumentType == "Switch")
                    stringResource(R.string.dialog_edit_calibration_txt_range_switch)
                else
                    stringResource(R.string.dialog_edit_calibration_txt_range_transmitter)} ${selectedInstrument.getRangeVerification()}",
                    modifier = Modifier.padding(start = 10.dp))
                Card {
                    Column(modifier = Modifier.padding(start = 5.dp, end = 5.dp, bottom = 5.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically) {
                            if (selectedInstrument.instrumentType != "Switch")
                                Text(text = stringResource(R.string.dialog_edit_calibration_label_pattern), textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium)
                            Text(text = stringResource(R.string.dialog_edit_calibration_title_found_value) , textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium)
                            Text(text = stringResource(R.string.dialog_edit_calibration_title_left_value), textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                        if (selectedInstrument.instrumentType == "Switch"){
                            val colDates = calibrationValues.getOrDefault("values 0", emptyMap())
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ){
                                EditTextCalibration(
                                    value = colDates.getOrDefault("value 0", ""),
                                    label = stringResource(R.string.dialog_edit_calibration_label_found_value),
                                    modifier = Modifier.weight(1f),
                                    onValueChange = {viewModel.onEditCalibrationChange(0,0, it)})
                                VerticalDivider()
                                EditTextCalibration(
                                    value = colDates.getOrDefault("value 1", ""),
                                    label = stringResource(R.string.dialog_edit_calibration_label_found_value),
                                    modifier = Modifier.weight(1f),
                                    onValueChange = {viewModel.onEditCalibrationChange(0,1, it)})

                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ){
                                EditTextCalibration(
                                    value = colDates.getOrDefault("value 2", ""),
                                    label = stringResource(R.string.dialog_edit_calibration_label_reposition_value),
                                    modifier = Modifier.weight(1f),
                                    onValueChange = {viewModel.onEditCalibrationChange(0,2, it)})
                                VerticalDivider()
                                EditTextCalibration(
                                    value = colDates.getOrDefault("value 3", ""),
                                    label = stringResource(R.string.dialog_edit_calibration_label_reposition_value),
                                    modifier = Modifier.weight(1f),
                                    onValueChange = {viewModel.onEditCalibrationChange(0,3, it)})

                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(
                                    text = stringResource(R.string.dialog_edit_calibration_title_drive),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(0.6f),
                                    style = MaterialTheme.typography.bodySmall)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.weight(0.4f),
                                ) {
                                    RadioButton(
                                        selected = (calibrationValues["values 0"]?.get("value 4") == "Bajada"),
                                        onClick = { viewModel.onEditCalibrationChange(0,4, "Bajada") }
                                    )
                                    Text(
                                        text = stringResource(R.string.dialog_edit_calibration_label_rb_down),
                                        modifier = Modifier.padding(start = 2.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.weight(0.4f)) {
                                    RadioButton(
                                        selected = (calibrationValues["values 0"]?.get("value 4") == "Subida"),
                                        onClick = { viewModel.onEditCalibrationChange(0,4, "Subida") }
                                    )
                                    Text(
                                        text = stringResource(R.string.dialog_edit_calibration_label_rb_up),
                                        modifier = Modifier.padding(start = 2.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }


                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(
                                    text = stringResource(R.string.dialog_edit_calibration_title_electrical_contact),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(0.6f),
                                    style = MaterialTheme.typography.bodySmall)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.weight(0.4f)
                                ) {
                                    RadioButton(
                                        selected = (calibrationValues["values 0"]?.get("value 5") == "NC"),
                                        onClick = { viewModel.onEditCalibrationChange(0,5, "NC") }
                                    )
                                    Text(
                                        text = "NC",
                                        modifier = Modifier.padding(start = 2.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.weight(0.4f)
                                ) {
                                    RadioButton(
                                        selected = (calibrationValues["values 0"]?.get("value 5") == "NO"),
                                        onClick = { viewModel.onEditCalibrationChange(0,5, "NO") }
                                    )
                                    Text(
                                        text = "NO",
                                        modifier = Modifier.padding(start = 2.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        else{
                            val maxRow = when(selectedInstrument.reportType){
                                stringArrayResource(R.array.report_type_options)[6]-> 1
                                stringArrayResource(R.array.report_type_options)[3],
                                stringArrayResource(R.array.report_type_options)[4] -> 2
                                stringArrayResource(R.array.report_type_options)[0] -> 4
                                else -> 8
                            }

                            for (row in 0..maxRow){
                                val imeAction = if (row == maxRow) {
                                    ImeAction.Done
                                } else ImeAction.Next

                                val keyboardActions = KeyboardActions(
                                    onNext = { localFocusManager.moveFocus(FocusDirection.Down) },
                                    onDone = {
                                        localFocusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween){
                                    val colDates = calibrationValues.getOrDefault("values $row", emptyMap())
                                    EditTextCalibration(
                                        label = stringResource(R.string.dialog_edit_calibration_label_pattern),
                                        value = colDates.getOrDefault("value 0", getPoint(row, selectedInstrument)),
                                        modifier = Modifier.scale(0.9f).weight(1f),
                                        keyboardActions = keyboardActions,
                                        imeAction = imeAction,
                                        onValueChange = {viewModel.onEditCalibrationChange(row, 0, it)})
                                    EditTextCalibration(
                                        label = stringResource(R.string.dialog_edit_calibration_label_found_value),
                                        value = colDates.getOrDefault("value 1", ""),
                                        modifier = Modifier.scale(0.9f).weight(1f),
                                        keyboardActions = keyboardActions,
                                        imeAction = imeAction,
                                        onValueChange = {viewModel.onEditCalibrationChange(row, 1, it)})
                                    EditTextCalibration(
                                        label = stringResource(R.string.dialog_edit_calibration_label_found_value),
                                        value = colDates.getOrDefault("value 2", ""),
                                        modifier = Modifier.scale(0.9f).weight(1f),
                                        keyboardActions = keyboardActions,
                                        imeAction = imeAction,
                                        onValueChange = {viewModel.onEditCalibrationChange(row, 2, it)})
                                }
                            }
                            if (maxRow == 4 || maxRow == 8){
                                TextButton(onClick = {}) { Text(stringResource(R.string.dialog_edit_calibration_txt_autoComplete)) }
                            }

                            if (selectedInstrument.magnitude == "Flujo Multivariable"){
                                for (index in 0..1){
                                    Text(
                                        text =
                                        "Variable ${index+2}: ${if (index == 0)selectedInstrument.getRangeVerificationTV()
                                        else
                                            selectedInstrument.getRangeVerificationQV()}",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium)
                                    for (row in if (index == 0)10..18 else 20..28){
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ){
                                            val colDates = calibrationValues.getOrDefault("values $row", emptyMap())
                                            EditTextCalibration(
                                                value = colDates.getOrDefault("value 0", ""),
                                                modifier = Modifier.weight(1f),
                                                onValueChange = {viewModel.onEditCalibrationChange(row, 0, it)})
                                            VerticalDivider()
                                            EditTextCalibration(
                                                value = colDates.getOrDefault("value 1", ""),
                                                modifier = Modifier.weight(1f),
                                                onValueChange = {viewModel.onEditCalibrationChange(row, 1, it)})
                                            VerticalDivider()
                                            EditTextCalibration(
                                                value = colDates.getOrDefault("value 2", ""),
                                                modifier = Modifier.weight(1f),
                                                onValueChange = {viewModel.onEditCalibrationChange(row, 2, it)})
                                        }
                                    }
                                }
                            }

                            if (selectedInstrument.reportType.contains("ZR")){
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround) {
                                    Text(text = stringResource(R.string.dialog_edit_calibration_label_item), textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium)
                                    Text(text = stringResource(R.string.dialog_edit_calibration_label_before), textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium)
                                    Text(text = stringResource(R.string.dialog_edit_calibration_label_after), textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium)
                                    Text(text = stringResource(R.string.dialog_edit_calibration_label_status), textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }


                        }
                    }
                }




            }
        }
    }
}

fun getPoint(row: Int, instrument: InstrumentEntity): String {
    var patron = 0f
    val values = arrayOf(4,8,12,16,20,16,12,8,4)
    if (instrument.getSpan() > 0 && instrument.instrumentType != "Sensor") {
        patron = ((((values[row].toFloat() - 4f) / 16f) *
                if (instrument.magnitude == "Flujo Multivariable") instrument.getSpanSV()
                else instrument.getSpan()) +
                if (instrument.magnitude == "Flujo Multivariable") instrument.verificationMinSV.toFloat()
                else instrument.verificationMin.toFloat())
    }else
        return ""

    return (String.format("%.1f", patron))
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ObservationsDialog(
    observations: String,
    onObservationsChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit) {

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {onDismiss()
                    onConfirm()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancelar")
            }
        },
        title = { Text(
            text = stringResource(id = R.string.observations_dialog_title),
            style = MaterialTheme.typography.titleMedium
        ) },
        text = {
            Column {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = observations,
                    onValueChange = {onObservationsChange(it)},
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                )
            }
        })

}

@Composable
fun MultiChoiceConfirmationDialog(
    patterns: List<PatternEntity>,
    calibrationPatterns: List<PatternEntity>,
    titleText: String,
    onConfirm: (List<PatternEntity>) -> Unit,
    onDismiss: () -> Unit
) {
    val (selectedOptions, selectOptions) = remember {
        mutableStateOf(calibrationPatterns) // (2)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedOptions)
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancelar")
            }
        },
        title = { Text(
            text = titleText,
            style = MaterialTheme.typography.titleMedium
        ) },
        text = {
            DialogContent(
                items = patterns,
                checkedOptions = selectedOptions,
                selectOptions = selectOptions
            )
        })

}

@Composable
private fun DialogContent(
    items: List<PatternEntity>,
    checkedOptions: List<PatternEntity>,
    selectOptions: (List<PatternEntity>) -> Unit,
) {

    Column {
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .height(400.dp)) {
            items.forEach {currentItem ->
                val isChecked = currentItem in checkedOptions // (1)
                item {
                    ItemRow(
                        item = currentItem,
                        checked = isChecked,
                        onValueChange = { checked ->
                            val checkedItems = checkedOptions.toMutableList()

                            if (checked)
                                checkedItems.add(currentItem) // (2)
                            else
                                checkedItems.remove(currentItem) // (3)

                            selectOptions(checkedItems) // (4)
                        })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ItemRow(
    item: PatternEntity,
    checked: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .toggleable(
                value = checked,
                onValueChange = onValueChange,
                role = Role.Checkbox
            )
            .fillMaxWidth()
            .padding(end = 10.dp)
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(52.dp)
                .padding(top = 3.dp, end = 6.dp, bottom = 3.dp)
                .background(getColor(item.certificate.broadcastDate))
        )
        Text(text = item.name)
        Spacer(modifier = Modifier.fillMaxWidth())
        Checkbox(checked = checked, onCheckedChange = null)
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = Color.Black,
        modifier = Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            //.padding(8.dp)
    )
}

