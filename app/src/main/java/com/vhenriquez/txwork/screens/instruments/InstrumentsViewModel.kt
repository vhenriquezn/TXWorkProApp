package com.vhenriquez.txwork.screens.instruments

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.common.snackbar.SnackbarMessage
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.navigation.Main
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class InstrumentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository
) : TXWorkViewModel() {
    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    var uiState = mutableStateOf(InstrumentsUiState())
        private set

    private val _business = MutableStateFlow<List<CompanyEntity>>(emptyList())
    val business = _business.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _instruments = MutableStateFlow(listOf<InstrumentEntity>())

    val instruments = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            repository.getInstrumentsFlow(id)
        }
        .combine(searchText.debounce(1000L)) { instruments, text ->
            if (text.isBlank()) {
                instruments.data?: emptyList()
            } else {
                instruments.data?.filter { it.doesMatchSearchQuery(text) }?: emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _instruments.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun deleteInstrument() {
        val instrumentId = uiState.value.selectedInstrument?.id
        launchCatching {
            when(val result = instrumentId?.let { repository.deleteInstrument(it) }){
                is Resource.Error -> {
                    SnackbarManager.showMessage(SnackbarMessage.StringSnackbar(result.message?:""))
                }
                else->{}
            }
            uiState.value = uiState.value.copy(showDeleteInstrumentDialog = false, selectedInstrument = null)
        }
    }

    fun onInstrumentActionClick(openScreen: (Any) -> Unit, index: Int, instrumentEntity: InstrumentEntity) {
        uiState.value = uiState.value.copy(selectedInstrument = instrumentEntity)
        when (index) {
            0 -> {//Copiar
                uiState.value = uiState.value.copy(selectedInstrument = null)
                launchCatching {
                    val instrumentMap = mapOf(
                        InstrumentEntity.TAG to "${instrumentEntity.tag}(1)",
                        InstrumentEntity.BUSINESS to instrumentEntity.business,
                        InstrumentEntity.BUSINESS_ID to instrumentEntity.businessId,
                        InstrumentEntity.COMPANY_APP_ID to instrumentEntity.companyAppId,
                        InstrumentEntity.AREA to instrumentEntity.area,
                        InstrumentEntity.MODEL to instrumentEntity.model,
                        InstrumentEntity.DAMPING to instrumentEntity.damping,
                        InstrumentEntity.BRAND to instrumentEntity.brand,
                        InstrumentEntity.OUTPUT to instrumentEntity.output,
                        InstrumentEntity.SENSOR_TYPE to instrumentEntity.sensorType,
                        InstrumentEntity.DESCRIPTION to instrumentEntity.description,
                        InstrumentEntity.INSTRUMENT_TYPE to instrumentEntity.instrumentType,
                        InstrumentEntity.MAGNITUDE to instrumentEntity.magnitude,
                        InstrumentEntity.VERIFICATION_MIN to instrumentEntity.verificationMin,
                        InstrumentEntity.VERIFICATION_MAX to instrumentEntity.verificationMax,
                        InstrumentEntity.VERIFICATION_UNIT to instrumentEntity.verificationUnit,
                        InstrumentEntity.REPORT_TYPE to instrumentEntity.reportType,
                        InstrumentEntity.MEASUREMENT_MIN to instrumentEntity.measurementMin,
                        InstrumentEntity.MEASUREMENT_MAX to instrumentEntity.measurementMax,
                        InstrumentEntity.MEASUREMENT_UNIT to instrumentEntity.measurementUnit
                    )
                    repository.saveInstrument(instrumentMap)
                }
            }
            1 -> {//Editar
                uiState.value = uiState.value.copy(selectedInstrument = null)
                openScreen(Main.EditInstrument(
                    instrumentId = instrumentEntity.id,
                    companyAppId = companyAppIdSelected.value))
            }
            2 -> {//Eliminar
                uiState.value = uiState.value.copy(showDeleteInstrumentDialog = true)
            }
            3 -> {//Cancelar
                uiState.value = uiState.value.copy(selectedInstrument = null)
            }
        }
    }


}