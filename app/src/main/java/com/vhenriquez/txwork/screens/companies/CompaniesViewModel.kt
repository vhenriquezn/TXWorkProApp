package com.vhenriquez.txwork.screens.companies

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.common.snackbar.SnackbarMessage
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.CompanyEntity
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
class CompaniesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository
) : TXWorkViewModel() {

    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _companies = MutableStateFlow(listOf<CompanyEntity>())

    var uiState = mutableStateOf(CompaniesUiState())
        private set

    val companies = flowOf( companyAppIdSelected.value)
        .flatMapLatest { id ->
            repository.getCompaniesFlow(id)
        }
        .combine(searchText.debounce(1000L)) { companies, text ->
            if (text.isBlank()) {
                companies.data?: emptyList()
            } else {
                companies.data?.filter { it.doesMatchSearchQuery(text) }?: emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _companies.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }


    fun deleteCompany() {
        val company = uiState.value.selectedCompany
        launchCatching {
            when(val result = company?.let { repository.deleteCompany(it.id) }){
                is Resource.Error -> {
                    SnackbarManager.showMessage(SnackbarMessage.StringSnackbar(result.message?:""))
                }
                is Resource.Success->{

                }
                else -> {}
            }
            uiState.value = uiState.value.copy(showDeleteCompanyDialog = false, selectedCompany = null)

        }
    }

    fun onCompanyActionClick(index: Int, company: CompanyEntity) {
        uiState.value = uiState.value.copy(selectedCompany = company)
        when (index) {
            0 -> {//Eliminar
                uiState.value = uiState.value.copy(showDeleteCompanyDialog = true)
            }
            1 -> {//Cancelar
                uiState.value = uiState.value.copy(selectedCompany = null)
            }
        }
    }

}
