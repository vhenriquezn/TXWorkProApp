package com.vhenriquez.txwork.screens.edit_company_app

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.repository.AuthRepository
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditCompanyAppViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firestoreRepository: FirestoreRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : TXWorkViewModel(){
    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    val company = mutableStateOf(CompanyEntity())
    private val companyMap = mutableMapOf<String,Any>()

    init {
        val companyId = savedStateHandle.get<String>("companyId")
        if (!companyId.isNullOrEmpty()) {
            launchCatching {
                company.value = firestoreRepository.getCompanyAppSelected(companyId) ?: CompanyEntity()
            }
        }

        launchCatching {
         userPreferencesRepository.userId.collect{
             _userId.value = it
         }
        }
    }

    fun onNameChange(newValue: String){
        company.value = company.value.copy(name = newValue)
        companyMap[CompanyEntity.NAME] = newValue
    }

    fun onWebSiteChange(newValue: String){
        company.value = company.value.copy(website = newValue)
        companyMap[CompanyEntity.WEBSITE] = newValue
    }

    fun onAddressChange(newValue: String ){
        company.value = company.value.copy(address = newValue)
        companyMap[CompanyEntity.ADDRESS] = newValue
    }

    fun onLogoChange(newValue: String){
        company.value = company.value.copy(logo = newValue)
        companyMap[CompanyEntity.LOGO] = newValue
    }

    fun onSaveData(popUp: () -> Unit) {
        launchCatching {
            val editedCompany = company.value
            if (editedCompany.name.isBlank() || editedCompany.website.isBlank() ||
                editedCompany.address.isBlank()){
                SnackbarManager.showMessage("Debes llenar todos los campos")
                return@launchCatching
            }
            popUp()
            if (editedCompany.id.isBlank()) {
                companyMap["ownerId"] = userId.value
                firestoreRepository.saveCompanyApp(companyMap)
            } else {
                firestoreRepository.updateCompanyApp(companyMap, editedCompany.id)
            }

        }
    }

}