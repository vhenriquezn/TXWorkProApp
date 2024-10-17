package com.vhenriquez.txwork.screens.activityDetail

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.repository.WorkerRepository
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.ImageEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.repository.StorageRepository
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
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class InstrumentsDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository,
    private val workerRepository: WorkerRepository,
    private val storageRepository: StorageRepository
) : TXWorkViewModel() {

    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    private val _instruments = MutableStateFlow(listOf<InstrumentEntity>())

    var uiState = mutableStateOf(ActivityDetailUiState())
        private set

    private val _selectedActivity = MutableStateFlow(ActivityEntity())
    val selectedActivity = _selectedActivity.asStateFlow()

    private val selectedInstrument = mutableStateOf(InstrumentEntity())

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    init {
        val activityIdSelected = savedStateHandle.get<String>("activityId")
        if (!activityIdSelected.isNullOrEmpty()) {
            launchCatching {
                _selectedActivity.value = repository.getActivitySelected(activityIdSelected) ?: ActivityEntity()
            }
        }
    }

    val instruments = selectedActivity
        .flatMapLatest { activity ->
            repository.getInstrumentsFlow(
                activityId = activity.id,
                companyId = companyAppIdSelected.value)
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

    fun deleteInstrumentToActivity() {
        launchCatching {
            when (val result = selectedInstrument.let {
                repository.deleteInstrumentToActivity(
                    it.value.id, selectedActivity.value.id
                )
            }) {
                is Resource.Success -> {
                    uiState.value = uiState.value.copy(showDeleteDialog = false)
                    selectedInstrument.value = InstrumentEntity()

                }

                is Resource.Error -> {
                    uiState.value = uiState.value.copy(showDeleteDialog = false)
                    selectedInstrument.value = InstrumentEntity()
                    result.message?.let { SnackbarManager.showMessage(it) }
                }

                else -> {}
            }
        }
    }

    fun generateReport(context: Context) {
        selectedInstrument.let {
            selectedActivity.let { it1 ->
                workerRepository.generateReport(
                    context, it.value,
                    it1.value
                )
            }
        }

    }

    fun onInstrumentActionClick(
        openScreen: (Any) -> Unit,
        index: Int,
        instrumentEntity: InstrumentEntity
    ) {
        selectedInstrument.value = instrumentEntity
        when (index) {
            0 -> {//Copiar
                selectedInstrument.value = InstrumentEntity()
                //openScreen(Main.EditActivity(activityEntity.id))
            }

            1 -> {//Editar
                openScreen(Main.EditInstrument(
                    instrumentId = instrumentEntity.id,
                    companyAppId = companyAppIdSelected.value))
            }

            2 -> {//Eliminar
                selectedInstrument.value = instrumentEntity
                uiState.value = uiState.value.copy(showDeleteDialog = true)
            }

            5 -> {//Cancelar
                selectedInstrument.value = InstrumentEntity()
            }
        }

    }

    fun uploadImage(uriImage: Uri, instrumentEntity: InstrumentEntity) {

        val imageMap = mutableMapOf<String,Any>(
            ImageEntity.NAME to instrumentEntity.tag,
            ImageEntity.PHOTO_URL to uriImage.toString())
        launchCatching {
            val data = repository.saveImage(imageMap, instrumentEntity.id)
            if (data is Resource.Success){
                data.data?.let {imageId->
                    val result = storageRepository.uploadImage(uriImage, instrumentEntity.tag)
                    if (result is Resource.Success) {
                        result.data?.let {
                            imageMap[ImageEntity.PHOTO_URL] = it
                            repository.updateImage(imageMap, imageId, instrumentEntity.id)
                        }
                    }
                    if (result is Resource.Error) {
                        SnackbarManager.showMessage(result.message.toString())
                        return@launchCatching
                    }
                }
            }
            if (data is Resource.Error) {
                SnackbarManager.showMessage(data.message.toString())
                return@launchCatching
            }
        }
    }
    }