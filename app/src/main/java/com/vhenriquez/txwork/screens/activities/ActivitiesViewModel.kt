package com.vhenriquez.txwork.screens.activities


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.common.snackbar.SnackbarMessage
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.repository.UserPreferencesRepository
import com.vhenriquez.txwork.navigation.Main
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ActivitiesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository,
    userPreferencesRepository: UserPreferencesRepository
) : TXWorkViewModel() {

    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    private val _activitiesDataStore = MutableStateFlow(mapOf<String,String>())
    private val activitiesDataStore = _activitiesDataStore.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    var uiState = mutableStateOf(ActivitiesUiState())
        private set

    private val _activities = MutableStateFlow(listOf<ActivityEntity>())

    val activities = activitiesDataStore
        .flatMapLatest {
            repository.getActivitiesFlow(companyAppIdSelected.value, it["filter"], it["userId"])
        }
        .combine(searchText.debounce(1000L)) { activities, text ->
            if (text.isBlank()) {
                activities.data?: emptyList()
            } else {
                activities.data?.filter { it.doesMatchSearchQuery(text) }?: emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _activities.value
        )

    init {
        launchCatching {
            userPreferencesRepository.activitiesDataStore.collect{
                _activitiesDataStore.value = it
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun deleteActivity() {
        val activityId = uiState.value.selectedActivity?.id
        launchCatching {
            when(val result = activityId?.let { repository.deleteActivity(it) }){
                is Resource.Error -> {
                    SnackbarManager.showMessage(SnackbarMessage.StringSnackbar(result.message?:""))
                }
                else->{}
            }
            uiState.value = uiState.value.copy(showDeleteActivityDialog = false, selectedActivity = null)
        }
    }

    private fun toggleStatusActivity() {
        launchCatching {
            val activity = uiState.value.selectedActivity
            when(val result = activity?.id?.let { repository.toggleStatusActivity(it, if (activity.status == "open") "closed" else "open") }){
                is Resource.Error -> {
                    SnackbarManager.showMessage(SnackbarMessage.StringSnackbar(result.message?:""))
                }
                else->{}
            }
            uiState.value = uiState.value.copy(selectedActivity = null)
        }
    }

    fun onActivityActionClick(openScreen: (Any) -> Unit, index: Int, activityEntity: ActivityEntity) {
        uiState.value = uiState.value.copy(selectedActivity = activityEntity)
        when (index) {
            0 -> {//Editar
                uiState.value = uiState.value.copy(selectedActivity = null)
                openScreen(Main.EditActivity(activityEntity.id, companyAppIdSelected.value))
            }
            1 -> {//Eliminar
                uiState.value = uiState.value.copy(showDeleteActivityDialog = true)
            }
            2 -> {//Usuarios
                uiState.value = uiState.value.copy(selectedActivity = null)
            }
            3 -> {//Reportes
                uiState.value = uiState.value.copy(selectedActivity = null)
            }
            4 -> {//Cerrar Actividad
                toggleStatusActivity()
                }
            5 -> {//Cancelar
                uiState.value = uiState.value.copy(selectedActivity = null)
            }
        }
    }
}