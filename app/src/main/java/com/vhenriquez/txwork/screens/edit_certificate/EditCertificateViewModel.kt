package com.vhenriquez.txwork.screens.edit_certificate

import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.repository.StorageRepository
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
class EditCertificateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firestoreRepository: FirestoreRepository,
    private val storageRepository: StorageRepository
) : TXWorkViewModel(){

    private val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    val certificate = mutableStateOf(CertificateEntity())
    private val certificateMap = mutableMapOf<String,Any>()

    private val _families = MutableStateFlow(listOf<String>())


    var uiState = mutableStateOf(EditCertificateUiState())
        private set

    val families = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            flowOf(firestoreRepository.getFamilies(id))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _families.value
        )

    init {
        val certificateId = savedStateHandle.get<String>("certificateId")
        if (!certificateId.isNullOrEmpty() ) {
            launchCatching {
                certificate.value = firestoreRepository.getCertificateSelected(certificateId) ?: CertificateEntity()
            }
        }
    }

    fun onNameChange(newValue: String){
        certificate.value = certificate.value.copy(name = newValue)
        certificateMap[CertificateEntity.NAME] = newValue
    }

    fun onCertificateIdChange(newValue: String) {
        certificate.value = certificate.value.copy(certificateId = newValue)
        certificateMap[CertificateEntity.CERTIFICATE_ID] = newValue
    }

    fun onFamilyChange(newValue: String) {
        uiState.value = uiState.value.copy(expandedFamily = false)
        certificate.value = certificate.value.copy(family = newValue)
        certificateMap[CertificateEntity.FAMILY] = newValue
    }

    fun onLaboratoryChange(newValue: String) {
        certificate.value = certificate.value.copy(laboratory = newValue)
        certificateMap[CertificateEntity.LABORATORY] = newValue
    }

    fun onDateChange(newValue: String) {
        certificate.value = certificate.value.copy(broadcastDate = newValue)
        certificateMap[CertificateEntity.BROADCAST_DATE] = newValue
    }

    fun onCertificateUrlChange(newValue: String) {
        certificate.value = certificate.value.copy(pdfUrl = newValue)
        certificateMap[CertificateEntity.PDF_URL] = newValue
    }


    fun onSaveData(popUp: () -> Unit) {
        launchCatching {
            val editedCertificate = certificate.value
            if (editedCertificate.name.isBlank() || editedCertificate.laboratory.isBlank() ||
                editedCertificate.family.isBlank() || editedCertificate.certificateId.isBlank() ||
                editedCertificate.broadcastDate.isBlank()){
                SnackbarManager.showMessage("Debes llenar todos los campos")
                return@launchCatching
            }
            if (!editedCertificate.pdfUrl.contains("https://")) {
                val result = storageRepository.uploadCertificate(editedCertificate.pdfUrl.toUri())
                if (result is Resource.Success) {
                    certificateMap[CertificateEntity.PDF_URL] = result.data?:""
                }
                if (result is Resource.Error){
                    SnackbarManager.showMessage(result.message.toString())
                    return@launchCatching
                }
            }
            popUp()

            if (editedCertificate.id.isBlank()) {
                certificateMap[CertificateEntity.COMPANY_APP_ID] = companyAppIdSelected.value
                firestoreRepository.saveCertificate(certificateMap)
            } else {
                firestoreRepository.updateCertificate(certificateMap, editedCertificate.certificateId)
            }

        }
    }




}