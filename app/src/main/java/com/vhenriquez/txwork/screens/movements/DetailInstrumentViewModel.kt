package com.vhenriquez.txwork.screens.movements

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CalibrationEntity
import com.vhenriquez.txwork.model.ImageEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.PatternEntity
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class DetailInstrumentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository,
    private val storage: StorageRepository
) : TXWorkViewModel() {

    var uiState = mutableStateOf(InstrumentDetailUiState())
        private set

    private val selectedActivity = mutableStateOf(ActivityEntity())
    val selectedInstrument = mutableStateOf(InstrumentEntity())
    private val selectedInstrumentMap = mutableMapOf<String,Any>()
    val calibrationInfo = mutableStateOf(CalibrationEntity())
    private val calibrationInfoMap = mutableMapOf<String,Any>()
    val calibrationValues = mutableStateOf(mapOf<String, Map<String, String>>())
    val patterns = mutableStateOf(listOf<PatternEntity>())
    val images = mutableStateOf(listOf<ImageEntity>())

    init {
        val activityId = savedStateHandle.get<String>("activityId")
        if (!activityId.isNullOrEmpty()) {
            launchCatching {
                selectedActivity.value = repository.getActivitySelected(activityId) ?: ActivityEntity()
            }
        }

        val companyAppIdSelected = savedStateHandle.get<String>("companyAppId")
        if (!companyAppIdSelected.isNullOrEmpty()){
            launchCatching {
                patterns.value = repository.getAllPatterns(companyId = companyAppIdSelected) ?: emptyList()
            }
        }
    }

    private fun observeImages(instrumentId: String) {
        launchCatching {
            repository.getImagesFlow(instrumentId = instrumentId)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            images.value = result.data ?: emptyList()
                        }

                        is Resource.Error -> {}
                        else -> {}
                    }
                }
        }
    }

    fun onObservationsInstrumentChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(observations = newValue)
        selectedInstrumentMap[InstrumentEntity.OBSERVATIONS] = newValue
    }

    fun onBrandChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(brand = newValue)
        selectedInstrumentMap[InstrumentEntity.BRAND] = newValue
    }
    fun onModelChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(model = newValue)
        selectedInstrumentMap[InstrumentEntity.MODEL] = newValue
    }
    fun onSerialChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(serial = newValue)
        selectedInstrumentMap[InstrumentEntity.SERIAL] = newValue

    }
    fun onDampingChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(damping = newValue)
        selectedInstrumentMap[InstrumentEntity.DAMPING] = newValue
    }
    fun onMeasurementMinChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(measurementMin = newValue)
        selectedInstrumentMap[InstrumentEntity.MEASUREMENT_MIN] = newValue
    }
    fun onMeasurementMaxChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(measurementMax = newValue)
        selectedInstrumentMap[InstrumentEntity.MEASUREMENT_MAX] = newValue
    }
    fun onMeasurementUnitChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(measurementUnit = newValue)
        selectedInstrumentMap[InstrumentEntity.MEASUREMENT_UNIT] = newValue
    }
    fun onVerificationMinChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationMin = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_MIN] = newValue
    }
    fun onVerificationMaxChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationMax = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_MAX] = newValue
    }
    fun onVerificationUnitChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationUnit = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_UNIT] = newValue
    }
    fun onVerificationMinSVChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationMinSV = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_MIN_SV] = newValue
    }
    fun onVerificationMaxSVChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationMaxSV = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_MAX_SV] = newValue
    }
    fun onVerificationUnitSVChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationUnitSV = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_UNIT_SV] = newValue
    }
    fun onVerificationMinTVChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationMinTV = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_MIN_TV] = newValue
    }
    fun onVerificationMaxTVChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationMaxTV = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_MAX_TV] = newValue
    }
    fun onVerificationUnitTVChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationUnitTV = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_UNIT_TV] = newValue
    }
    fun onVerificationMinQVChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationMinQV = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_MIN_QV] = newValue
    }
    fun onVerificationMaxQVChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationMaxQV = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_MAX_QV] = newValue
    }
    fun onVerificationUnitQVChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(verificationUnitQV = newValue)
        selectedInstrumentMap[InstrumentEntity.VERIFICATION_UNIT_QV] = newValue
    }
    fun onOutputChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(output = newValue)
        selectedInstrumentMap[InstrumentEntity.OUTPUT] = newValue
    }
    fun onSensorTypeChange(newValue: String){
        selectedInstrument.value = selectedInstrument.value.copy(sensorType = newValue)
        selectedInstrumentMap[InstrumentEntity.SENSOR_TYPE] = newValue
    }
    fun onServiceChange(newValue: String){
        uiState.value = uiState.value.copy(showDialogService = false)
        calibrationInfo.value = calibrationInfo.value.copy(service = newValue)
        calibrationInfoMap[CalibrationEntity.SERVICE] = newValue
        saveCalibrationInfoUpdate()
    }
    fun onCalibrationTypeChange(newValue: String){
        uiState.value = uiState.value.copy(showDialogCalibrationType = false)
        calibrationInfo.value = calibrationInfo.value.copy(calibrationType = newValue)
        calibrationInfoMap[CalibrationEntity.CALIBRATION_TYPE] = newValue
        saveCalibrationInfoUpdate()
    }
    fun onObservationsCalibrationChange(newValue: String){
        calibrationInfo.value = calibrationInfo.value.copy(observation = newValue)
        calibrationInfoMap[CalibrationEntity.OBSERVATION] = newValue
    }

    fun saveInstrumentUpdate(){
        uiState.value = uiState.value.copy(showDialogEditInfo = false)
        launchCatching {
            val result= repository.updateInstrument(selectedInstrumentMap, selectedInstrument.value.id)
            if (result is Resource.Success){
                selectedInstrumentMap.clear()
            }
        }
    }

    fun saveCalibrationInfoUpdate(){
        launchCatching {
            val result = repository.updateCalibrationInfo(selectedInstrument.value.id, selectedActivity.value.id, calibrationInfoMap)
            if (result is Resource.Success){
                calibrationInfoMap.clear()
            }
        }
    }

    fun setActions(selectedPatterns: List<PatternEntity>) {
        uiState.value = uiState.value.copy(showDialogPatterns = false)
        calibrationInfo.value = calibrationInfo.value.copy(patternEntities = selectedPatterns.toMutableList())
        calibrationInfoMap[CalibrationEntity.PATTERNS_ENTITY]= selectedPatterns.toMutableList()
        saveCalibrationInfoUpdate()
    }

    fun getSelectedInstrument(instrumentId: String) {
        launchCatching {
            repository.getInstrumentSelected(instrumentId).apply {
                if (this != null) {
                    selectedInstrument.value = this
                    calibrationInfo.value = this.calibrations[savedStateHandle.get<String>("activityId")] ?: CalibrationEntity()
                }
            }
            observeImages(instrumentId)
        }
    }

    fun onEditCalibrationChange(row: Int, col: Int, newValue: String) {
        calibrationValues.value = calibrationValues.value.toMutableMap().apply {
                val updatedRow = this["values $row"]?.toMutableMap() ?:
                mutableMapOf()
                updatedRow["value $col"] = newValue
            this["values $row"] = updatedRow
            }
    }

    fun onSetCalibrationValues(reportOptions: Array<String>) {
        calibrationValues.value = calibrationInfo.value.calibrationValues?: mapOf()
        if (selectedInstrument.value.instrumentType == "Switch"){
            if (calibrationValues.value.isEmpty())
                calibrationValues.value = mapOf("values 0" to
                            mapOf("value 0" to "",
                                "value 1" to "",
                                "value 2" to "",
                                "value 3" to "",
                                "value 4" to "Bajada",
                                "value 5" to "NC"))

        }else{
            val maxRow = when(selectedInstrument.value.reportType){
                reportOptions[6]-> 1
                reportOptions[3], reportOptions[4] -> 2
                reportOptions[0] -> 4
                else -> 8
            }
            val values = arrayOf(4f,8f,12f,16f,20f,16f,12f,8f,4f)
            calibrationValues.value = calibrationValues.value.toMutableMap().apply {
                for (row in 0..maxRow){
                    var patron = 0f
                    if(selectedInstrument.value.getSpan()>0 && selectedInstrument.value.instrumentType != "Sensor"){
                        patron = ((((values[row]-4f)/16f)*
                                if (selectedInstrument.value.magnitude == "Flujo Multivariable") selectedInstrument.value.getSpanSV()
                                else selectedInstrument.value.getSpan())+
                                if (selectedInstrument.value.magnitude == "Flujo Multivariable") selectedInstrument.value.verificationMinSV.toFloat()
                                else selectedInstrument.value.verificationMin.toFloat())
                    }
                    val updatedRow = this["values $row"]?.toMutableMap() ?:
                    mutableMapOf()
                    if (updatedRow["value 0"].isNullOrEmpty())
                        updatedRow["value 0"] = patron.toString()
                    if (updatedRow["value 1"].isNullOrEmpty())
                        updatedRow["value 1"] = ""
                    if (updatedRow["value 2"].isNullOrEmpty())
                        updatedRow["value 2"] = ""
                    this["values $row"] = updatedRow
                }
                if (selectedInstrument.value.magnitude == "Flujo Multivariable"){
                    for (index in 0..1){
                        val addRow = if (index == 0) 10 else 20
                        for (row in 0..maxRow){
                            var patron = 0f
                            if (if (index==0)selectedInstrument.value.getSpanTV() > 0 else selectedInstrument.value.getSpanQV() > 0){
                                patron = ((((values[row]-4f)/16f)*(if (index==0)selectedInstrument.value.getSpanTV()
                                else selectedInstrument.value.getSpanQV())) + (if (index==0)selectedInstrument.value.verificationMinTV.toFloat()
                                else selectedInstrument.value.verificationMinQV.toFloat()))}

                            val updatedRow = this["values ${row+addRow}"]?.toMutableMap() ?:
                            mutableMapOf()
                            if (updatedRow["value 0"].isNullOrEmpty())
                                updatedRow["value 0"] = patron.toString()
                            if (updatedRow["value 1"].isNullOrEmpty())
                                updatedRow["value 1"] = ""
                            if (updatedRow["value 2"].isNullOrEmpty())
                                updatedRow["value 2"] = ""
                            this["values ${row+addRow}"] = updatedRow
                        }

                    }
                }
            }
        }
    }

    fun saveCalibrationDates() {
        uiState.value = uiState.value.copy(showDialogCalibration = false)
        calibrationInfo.value = calibrationInfo.value.copy(calibrationValues = calibrationValues.value)
        calibrationInfoMap[CalibrationEntity.CALIBRATION_VALUES] = calibrationValues.value
        launchCatching {
            val result = repository.updateCalibrationInfo(selectedInstrument.value.id, selectedActivity.value.id, calibrationInfoMap)
            if (result is Resource.Success){
                calibrationInfoMap.clear()
            }
        }
    }

    fun deleteImage(image: ImageEntity) {
        launchCatching {
            val result = repository.deleteImage(image.id, selectedInstrument.value.id)
            if (result is Resource.Success){
                if (image.photoUrl.contains("https://"))
                    storage.deleteFileFromUrl(image.photoUrl)
                else {

                }
            }
        }
    }
}
