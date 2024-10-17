package com.vhenriquez.txwork.screens.edit_company

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditCompanyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firestoreRepository: FirestoreRepository
) : TXWorkViewModel(){

    private val companyAppIdSelected = mutableStateOf(savedStateHandle.get<String>("companyAppId")?:"")

    val company = mutableStateOf(CompanyEntity())
    private val companyMap = mutableMapOf<String,Any>()

    init {
        val companyIdSelected = savedStateHandle.get<String>("companyId")
        if (!companyIdSelected.isNullOrEmpty()) {
            launchCatching {
                company.value = firestoreRepository.getCompanySelected(companyIdSelected) ?: CompanyEntity()
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
                companyMap[CompanyEntity.COMPANY_APP_ID] = companyAppIdSelected.value
                firestoreRepository.saveCompany(companyMap)
            } else {
                firestoreRepository.updateCompany(companyMap, editedCompany.id)
            }

        }
    }

}