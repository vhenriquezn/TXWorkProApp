package com.vhenriquez.txwork.screens.activityDetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.UserEntity
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
class AddUsersToActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository
) : TXWorkViewModel() {

    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    val activityId = savedStateHandle.get<String>("activityId")
    val businessId = savedStateHandle.get<String>("businessId")

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _activitySelected = MutableStateFlow(ActivityEntity())
    val activitySelected = _activitySelected.asStateFlow()

    private val _users = MutableStateFlow(listOf<UserEntity>())

    val users = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            repository.getUsersFlow(
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
            _users.value
        )

    init {
        launchCatching {
            repository.getActivitySelected(activityId).apply {
                _activitySelected.value = this!!
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun addUserToActivity(user: UserEntity, exist: Boolean) {
        launchCatching {
            if (exist)
                activityId?.let {
                    repository.deleteUserToActivity(user.id, it).apply {
                        if (this.data == true) {
                            val usersActivity = activitySelected.value.users.toMutableList().apply {
                                remove(user.id)
                            }
                            _activitySelected.value = activitySelected.value.copy(users = usersActivity)
                        }
                    }
                }
            else
                activityId?.let {
                    repository.addUserToActivity(user.id, it).apply {
                        if (this.data == true) {
                            val usersActivity = activitySelected.value.users.toMutableList().apply {
                                add(user.id)
                            }
                            _activitySelected.value = activitySelected.value.copy(users = usersActivity)
                        }
                    }
                }
        }
    }
}