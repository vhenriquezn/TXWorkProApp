package com.vhenriquez.txwork.screens.edit_instrument

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EditInstrumentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firestoreRepository: FirestoreRepository,
) : TXWorkViewModel(){

    private val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    val instrument = mutableStateOf(InstrumentEntity())
    private val instrumentMap = mutableMapOf<String,Any>()

    private val _business = MutableStateFlow(listOf<CompanyEntity>())

    var uiState = mutableStateOf(EditInstrumentUiState())
        private set

    val business = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            flowOf(firestoreRepository.getBusiness(id))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _business.value
        )

    init {
        val instrumentIdSelected = savedStateHandle.get<String>("instrumentId")
        if (!instrumentIdSelected.isNullOrEmpty()) {
            launchCatching {
                instrument.value = firestoreRepository.getInstrumentSelected(instrumentIdSelected) ?: InstrumentEntity()
            }
        }
    }

    fun onTagChange(newValue: String){
        instrument.value = instrument.value.copy(tag = newValue)
        instrumentMap[InstrumentEntity.TAG] = newValue
    }

    fun onBusinessChange(newValue1: String, newValue2: String){
        instrument.value = instrument.value.copy(business = newValue1, businessId = newValue2)
        instrumentMap[InstrumentEntity.BUSINESS] = newValue1
        instrumentMap[InstrumentEntity.BUSINESS_ID] = newValue2
    }

    fun onDescriptionChange(newValue: String){
        instrument.value = instrument.value.copy(description = newValue)
        instrumentMap[InstrumentEntity.DESCRIPTION] = newValue
    }

    fun onAreaChange(newValue: String){
        instrument.value = instrument.value.copy(area = newValue)
        instrumentMap[InstrumentEntity.AREA] = newValue
    }

    fun onInstrumentTypeChange(newValue: String){
        instrument.value = instrument.value.copy(instrumentType = newValue)
        instrumentMap[InstrumentEntity.INSTRUMENT_TYPE] = newValue
    }

    fun onMagnitudeChange(newValue: String) {
        instrument.value = instrument.value.copy(magnitude = newValue)
        instrumentMap[InstrumentEntity.MAGNITUDE] = newValue
    }

    fun onVerificationMinChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationMin = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_MIN] = newValue
    }

    fun onVerificationMaxChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationMax = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_MAX] = newValue
    }

    fun onVerificationUnitChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationUnit = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_UNIT] = newValue
    }

    fun onVerificationMinSVChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationMinSV = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_MIN_SV] = newValue
    }

    fun onVerificationMaxSVChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationMaxSV = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_MAX_SV] = newValue
    }

    fun onVerificationUnitSVChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationUnitSV = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_UNIT_SV] = newValue
    }

    fun onVerificationMinTVChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationMinTV = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_MIN_TV] = newValue
    }

    fun onVerificationMaxTVChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationMaxTV = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_MAX_TV] = newValue
    }

    fun onVerificationUnitTVChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationUnitTV = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_UNIT_TV] = newValue
    }

    fun onVerificationMinQVChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationMinQV = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_MIN_QV] = newValue
    }

    fun onVerificationMaxQVChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationMaxQV = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_MAX_QV] = newValue
    }

    fun onVerificationUnitQVChange(newValue: String) {
        instrument.value = instrument.value.copy(verificationUnitQV = newValue)
        instrumentMap[InstrumentEntity.VERIFICATION_UNIT_QV] = newValue
    }

    fun onReportTypeChange(newValue: String) {
        instrument.value = instrument.value.copy(reportType = newValue)
        instrumentMap[InstrumentEntity.REPORT_TYPE] = newValue
    }

    fun onSaveData(popUp: () -> Unit) {
        launchCatching {
            val editedInstrument = instrument.value
            if(editedInstrument.tag.isBlank() || editedInstrument.business.isBlank() ||
                editedInstrument.description.isBlank() || editedInstrument.area.isBlank()){
                SnackbarManager.showMessage("Debes llenar todos los campos")
                return@launchCatching
            }
            popUp()
            if (editedInstrument.id.isBlank()) {
                instrumentMap[InstrumentEntity.COMPANY_APP_ID] = companyAppIdSelected.value
                firestoreRepository.saveInstrument(instrumentMap)
            } else {
                firestoreRepository.updateInstrument(instrumentMap, editedInstrument.id)
            }

        }
    }

}