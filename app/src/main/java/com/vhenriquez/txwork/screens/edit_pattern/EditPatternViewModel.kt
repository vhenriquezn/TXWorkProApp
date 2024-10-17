package com.vhenriquez.txwork.screens.edit_pattern

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.PatternEntity
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
class EditPatternViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firestoreRepository: FirestoreRepository,
) : TXWorkViewModel(){

    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    val pattern = mutableStateOf(PatternEntity())
    private val patternMap = mutableMapOf<String,Any>()

    private val _certificates = MutableStateFlow(listOf<CertificateEntity>())

    var uiState = mutableStateOf(EditPatternUiState())
        private set

    val certificates = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            flowOf(firestoreRepository.getAllCertificates(id))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _certificates.value
        )

    init {
        val patternIdSelected = savedStateHandle.get<String>("patternId")
        if (!patternIdSelected.isNullOrEmpty()) {
            launchCatching {
                pattern.value = firestoreRepository.getPatternSelected(patternIdSelected) ?: PatternEntity()
            }
        }
    }

    fun onNameChange(newValue: String){
        pattern.value = pattern.value.copy(name = newValue)
        patternMap[PatternEntity.NAME] = newValue
    }

    fun onBrandChange(newValue: String){
        pattern.value = pattern.value.copy(brand = newValue)
        patternMap[PatternEntity.BRAND] = newValue
    }

    fun onSerialChange(newValue: String){
        pattern.value = pattern.value.copy(serial = newValue)
        patternMap[PatternEntity.SERIAL] = newValue
    }

    fun onCertificateChange(newValue: CertificateEntity){
        pattern.value = pattern.value.copy(certificate = newValue, certificateId = newValue.id)
        patternMap[PatternEntity.CERTIFICATE] = newValue
        patternMap[PatternEntity.CERTIFICATE_ID] = newValue.id
    }


    fun onSaveData(popUp: () -> Unit) {
        launchCatching {
            val editedPattern = pattern.value
            if (editedPattern.name.isBlank() || editedPattern.brand.isBlank() ||
                editedPattern.serial.isBlank() || editedPattern.certificateId.isBlank()){
                SnackbarManager.showMessage("Debes llenar todos los campos")
                return@launchCatching
            }
            popUp()
            if (editedPattern.id.isBlank()) {
                patternMap[PatternEntity.COMPANY_APP_ID] = companyAppIdSelected.value
                firestoreRepository.savePattern(patternMap)
            } else {
                firestoreRepository.updatePattern(patternMap, editedPattern.id)
            }

        }
    }


}