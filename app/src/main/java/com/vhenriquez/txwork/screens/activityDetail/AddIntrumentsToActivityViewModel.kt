package com.vhenriquez.txwork.screens.activityDetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.InstrumentEntity
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

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddInstrumentsToActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository
) : TXWorkViewModel() {

    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    val activityId = savedStateHandle.get<String>("activityId")
    val businessId = savedStateHandle.get<String>("businessId")

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _instruments = MutableStateFlow(listOf<InstrumentEntity>())

    val instruments = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            repository.getInstrumentsFlow(
                businessId = businessId,
                companyId = id)
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

    fun addInstrumentToActivity(instrument: InstrumentEntity) {
        val exist = instrument.activities.contains(activityId)
        launchCatching {
            if (exist)
                repository.deleteInstrumentToActivity(instrument.id, activityId)
            else
                repository.addInstrumentToActivity(instrument.id, activityId)
        }
    }
}