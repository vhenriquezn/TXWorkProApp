package com.vhenriquez.txwork.screens.patterns

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.common.snackbar.SnackbarMessage
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.PatternEntity
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
class PatternsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository
) : TXWorkViewModel() {

    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    var uiState = mutableStateOf(PatternsUiState())
        private set

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _patterns = MutableStateFlow(listOf<PatternEntity>())

    val patterns = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            repository.getPatternsFlow(id)
        }
        .combine(searchText.debounce(1000L)) { patterns, text ->
            if (text.isBlank()) {
                patterns.data?: emptyList()
            } else {
                patterns.data?.filter { it.doesMatchSearchQuery(text) }?: emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _patterns.value
        )


    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onPatternActionClick(openScreen: (Any) -> Unit, index: Int, pattern: PatternEntity) {
        uiState.value = uiState.value.copy(selectedPattern = pattern)
        when (index) {
            0 -> {//Eliminar
                uiState.value = uiState.value.copy(showDeletePatternDialog = true)
            }
            1 -> {//Cancelar
                uiState.value = uiState.value.copy(selectedPattern = null)
            }
        }
    }

    fun deletePattern() {
        val patternId = uiState.value.selectedPattern?.id
        launchCatching {
            when(val result = patternId?.let { repository.deletePattern(it) }){
                is Resource.Error -> {
                    SnackbarManager.showMessage(SnackbarMessage.StringSnackbar(result.message?:""))
                }
                else->{}
            }
            uiState.value = uiState.value.copy(showDeletePatternDialog = false, selectedPattern = null)
        }
    }
}
