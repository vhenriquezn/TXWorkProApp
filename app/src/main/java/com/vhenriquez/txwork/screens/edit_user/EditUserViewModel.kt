package com.vhenriquez.txwork.screens.edit_user

import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.repository.StorageRepository
import com.vhenriquez.txwork.model.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firestoreRepository: FirestoreRepository,
    userPreferencesRepository: UserPreferencesRepository,
) : TXWorkViewModel(){

    private val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    private val _companyName = MutableStateFlow("")
    private val companyName = _companyName.asStateFlow()

    val user = mutableStateOf(UserEntity())
    private val userMap = mutableMapOf<String,Any>()
    var uiState = mutableStateOf(EditUserUiState())
        private set

    init {

        val userId = savedStateHandle.get<String>("userId")
        if (!userId.isNullOrEmpty() || companyAppIdSelected.value.isNotEmpty()) {
            launchCatching {
                user.value = firestoreRepository.getUserSelected(companyAppIdSelected.value, userId!!
                ) ?: UserEntity()
            }
        }

        launchCatching {
            userPreferencesRepository.companyNameSelected.collect{
                _companyName.value = it
            }
        }
    }

    fun onNameChange(newValue: String){
        user.value = user.value.copy(userName = newValue)
        userMap[UserEntity.USERNAME] = newValue
    }

    fun onEmailChange(newValue: String) {
        user.value = user.value.copy(email = newValue)
        userMap[UserEntity.EMAIL] = newValue
    }

    fun onInstrumentChange(newValue: Boolean) {
        val roles = user.value.roles.toMutableMap().apply {
            this["instruments"] = newValue
        }
        user.value = user.value.copy(roles = roles )
        userMap[UserEntity.ROLES] = roles
    }

    fun onBusinessChange(newValue: Boolean) {
        val roles = user.value.roles.toMutableMap().apply {
            this["business"] = newValue
        }
        user.value = user.value.copy(roles = roles )
        userMap[UserEntity.ROLES] = roles
    }

    fun onPatternsChange(newValue: Boolean) {
        val roles =user.value.roles.toMutableMap().apply {
            this["patterns"] = newValue
        }
        user.value = user.value.copy(roles = roles )
        userMap[UserEntity.ROLES] = roles
    }

    fun onToolsChange(newValue: Boolean) {
        val roles = user.value.roles.toMutableMap().apply {
            this["tools"] = newValue
        }
        user.value = user.value.copy(roles = roles )
        userMap[UserEntity.ROLES] = roles
    }

    fun onUsersChange(newValue: Boolean) {
        val roles = user.value.roles.toMutableMap().apply {
            this["users"] = newValue
        }
        user.value = user.value.copy(roles = roles )
        userMap[UserEntity.ROLES] = roles
    }

    fun onUserTypeChange(newValue: String) {
        user.value = user.value.copy(userType = newValue)
        userMap[UserEntity.USER_TYPE] = newValue
    }

    fun onSaveData(popUp: () -> Unit) {
        launchCatching {
            val editedUser = user.value
            if (editedUser.userName.isBlank()|| editedUser.email?.isBlank() == true){
                SnackbarManager.showMessage("Debes llenar todos los campos")
                return@launchCatching
            }

            if (editedUser.id.isBlank()) {
                val invitation = mapOf(
                    "companyId" to companyAppIdSelected,
                    "companyName" to companyName.value,
                    UserEntity.USER_TYPE to editedUser.userType,
                    UserEntity.ROLES to editedUser.roles,
                )
                firestoreRepository.sendInvitedUser(editedUser.email!!, invitation)
            } else {
                firestoreRepository.updateUser(companyAppIdSelected.value, userMap, editedUser.id)
            }
            popUp()

        }
    }
}