package com.vhenriquez.txwork.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.UserPreferencesRepository
import com.vhenriquez.txwork.navigation.Auth
import com.vhenriquez.txwork.navigation.Drawer
import com.vhenriquez.txwork.utils.TXWorkAppState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : TXWorkViewModel() {

    private val _user = MutableStateFlow(UserEntity())
    val user = _user.asStateFlow()

    private val _companyId = MutableStateFlow("")
    val companyId = _companyId.asStateFlow()

    private val _companies = MutableStateFlow(listOf<CompanyEntity>())

    private val _invitations = MutableStateFlow(listOf<Map<String, Any>>())

    var uiState = mutableStateOf(HomeUiState())
        private set

    var startDestinationDrawer = mutableStateOf<Any?>(null)
        private set

    val companies = user
        .flatMapLatest { user ->
            repository.getCompaniesAppFlow(user.id).map {
                it.data?: emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _companies.value
        )

    val invitations = user
        .flatMapLatest { user ->
            repository.getInvitationsFlow(user.id).map {
                it.data?: emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _invitations.value
        )

    init {
        launchCatching {
            userPreferencesRepository.userFlow.collect {
                _user.value = it
            }
        }
        launchCatching {
            userPreferencesRepository.companyIdSelected.collect{
                _companyId.value = it
                startDestinationDrawer.value = Drawer.Activities(it)
            }
        }
    }
    fun onSignOutClick(restartApp: (Any) -> Unit) {
        uiState.value = uiState.value.copy(showLogoutDialog = false)
        launchCatching {
            userPreferencesRepository.clearDatastore()
            restartApp(Auth)
        }
    }

    fun onTitleChange(newValue: String) {
        uiState.value = uiState.value.copy(title = newValue)
    }

    fun onAccept(invitation: Map<String, Any>, accept: Boolean) {
        val userMap = mapOf(
            UserEntity.USERNAME to user.value.userName,
            UserEntity.EMAIL to user.value.email,
            UserEntity.PHOTO_URL to user.value.photoUrl,
            UserEntity.ROLES to invitation[UserEntity.ROLES],
            UserEntity.USER_TYPE to invitation[UserEntity.USER_TYPE]
        )
        launchCatching {
            repository.aceptInvitation(userMap, accept, user.value.id, invitation)
        }
    }

    fun updateSelectedCompany(companyEntity: CompanyEntity, appState: TXWorkAppState) {
        launchCatching {
            userPreferencesRepository.updateCompanySelected(companyEntity)
            if (companyEntity.ownerId == user.value.id){
                userPreferencesRepository.updateRoles(mapOf( "instruments" to true, "users" to true,
                    "business" to true, "patterns" to true, "tools" to true))
            }else{
                val mUser = repository.getUserInCompanyApp(companyEntity.id, user.value.id)
                userPreferencesRepository.updateRoles(mUser["roles"] as Map<String, Boolean>)
            }
            appState.resetNavDrawer()
        }
    }
}