package com.vhenriquez.txwork.screens.edit_activity

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.repository.AuthRepository
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
class EditActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository,
    authRepository: AuthRepository
) : TXWorkViewModel(){

    private val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    val activity = mutableStateOf(ActivityEntity())
    private val activityMap = mutableMapOf<String,Any>()

    private val _business = MutableStateFlow(listOf<CompanyEntity>())

    private val userId = authRepository.currentUserId
    private val userName = authRepository.currentUserName
    var uiState = mutableStateOf(EditActivityUiState())
        private set

    val business = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            flowOf(repository.getBusiness(id))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _business.value
        )

    init {
        val activityIdSelected = savedStateHandle.get<String>("activityId")
        if (!activityIdSelected.isNullOrEmpty()) {
            launchCatching {
                activity.value = repository.getActivitySelected(activityIdSelected) ?: ActivityEntity()
            }
        }

//        launchCatching {
//            userPreferencesRepository.companyIdSelected.collect{
//                _companyId.value = it
//            }
//        }
    }


    fun onNameChange(newValue: String){
        activity.value = activity.value.copy(name = newValue)
        activityMap[ActivityEntity.NAME] = newValue
    }

    fun onDateChange(newValue: String){
        activity.value = activity.value.copy(date = newValue)
        activityMap[ActivityEntity.DATE] = newValue
    }

    fun onBusinessChange(newValue1: String, newValue2: String ){
        uiState.value = uiState.value.copy(expandedBusiness = false)
        activity.value = activity.value.copy(business = newValue1, businessId = newValue2)
        activityMap[ActivityEntity.BUSINESS] = newValue1
        activityMap[ActivityEntity.BUSINESS_ID] = newValue2
    }

    fun onWorkOrderChange(newValue: String){
        activity.value = activity.value.copy(workOrder = newValue)
        activityMap[ActivityEntity.WORK_ORDER] = newValue
    }

    fun onServiceOrderChange(newValue: String){
        activity.value = activity.value.copy(serviceOrder = newValue)
        activityMap[ActivityEntity.SERVICE_ORDER] = newValue
    }

    fun onSaveData(popUp: () -> Unit) {
        launchCatching {
            val editedActivity = activity.value
            if (editedActivity.name.isBlank() || editedActivity.business.isBlank() ||
                editedActivity.date.isBlank() || editedActivity.workOrder.isBlank() ||
                editedActivity.serviceOrder.isBlank()){
                SnackbarManager.showMessage("Debes llenar todos los campos")
                return@launchCatching
            }
            popUp()
            if (editedActivity.id.isBlank()) {
                activityMap[ActivityEntity.OWNER_ID] = userId
                activityMap[ActivityEntity.OWNER_NAME] = userName
                activityMap[ActivityEntity.STATUS] = "open"
                activityMap[ActivityEntity.COMPANY_APP_ID] = companyAppIdSelected.value
                repository.saveActivity(activityMap)
            } else {
                repository.updateActivity(activityMap, editedActivity.id)
            }

        }
    }

}