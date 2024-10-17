package com.vhenriquez.txwork.screens.users

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.model.repository.FirestoreRepository
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
class UsersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FirestoreRepository
) : TXWorkViewModel() {

    val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    var uiState = mutableStateOf(UsersUiState())
        private set

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _users = MutableStateFlow(listOf<UserEntity>())

    val users = flowOf(companyAppIdSelected.value)
        .flatMapLatest { id ->
            repository.getUsersFlow(id)
        }
        .combine(searchText.debounce(1000L)) { users, text ->
            if (text.isBlank()) {
                users.data?: emptyList()
            } else {
                users.data?.filter { it.doesMatchSearchQuery(text) }?: emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _users.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onUserActionClick(openScreen: (Any) -> Unit, index: Int, userEntity: UserEntity) {
        uiState.value = uiState.value.copy(selectedUser = userEntity)
        when (index) {
            0 -> {//Eliminar
                uiState.value = uiState.value.copy(showDeleteUserDialog = true)
            }
            1 -> {//Cancelar
                uiState.value = uiState.value.copy(selectedUser = null)
            }
        }
    }
}
