package com.vhenriquez.txwork.screens.certificates

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.common.snackbar.SnackbarMessage
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.repository.StorageRepository
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
class CertificatesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository,
    private val storage: StorageRepository
) : TXWorkViewModel() {

    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _certificates = MutableStateFlow(listOf<CertificateEntity>())

    val certificates = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            repository.getCertificatesFlow(id)
        }
        .combine(searchText.debounce(1000L)) { certificates, text ->
            if (text.isBlank()) {
                certificates.data?: emptyList()
            } else {
                certificates.data?.filter { it.doesMatchSearchQuery(text) }?: emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _certificates.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    var uiState = mutableStateOf(CertificatesUiState())
        private set

    fun deleteCertificate() {
        val certificate = uiState.value.selectedCertificate
        launchCatching {
            when(val result = certificate?.let { repository.deleteCertificate(it.id) }){
                is Resource.Error -> {
                    SnackbarManager.showMessage(SnackbarMessage.StringSnackbar(result.message?:""))
                }
                is Resource.Success->{
                    if (certificate.pdfUrl.contains("https://")){
                        storage.deleteFileFromUrl(certificate.pdfUrl)
                    }
                }
                else -> {}
            }
            uiState.value = uiState.value.copy(showDeleteCertificateDialog = false, selectedCertificate = null)

        }
    }


    fun onCertificateActionClick(index: Int, certificate: CertificateEntity) {
        uiState.value = uiState.value.copy(selectedCertificate = certificate)
        when (index) {
            0 -> {//Eliminar
                uiState.value = uiState.value.copy(showDeleteCertificateDialog = true)
            }
            1 -> {//Cancelar
                uiState.value = uiState.value.copy(selectedCertificate = null)
            }
        }
    }
}
