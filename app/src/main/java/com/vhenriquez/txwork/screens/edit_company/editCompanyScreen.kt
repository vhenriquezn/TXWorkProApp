package com.vhenriquez.txwork.screens.edit_company

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.EditTextInDialog
import com.vhenriquez.txwork.common.composable.PopupBox
import com.vhenriquez.txwork.model.CompanyEntity

@Composable
fun EditCompanyScreen(
    popUp: () -> Unit,
    viewModel: EditCompanyViewModel = hiltViewModel()){

    EditCompanyScreenContent(
        viewModel = viewModel,
        popUp = popUp,
        onDismiss = {popUp()}
    )
}

@Composable
fun EditCompanyScreenContent(
    viewModel: EditCompanyViewModel,
    popUp: () -> Unit,
    onDismiss: () -> Unit
){
    val companyEntity by viewModel.company
    PopupBox(
        title = if(companyEntity.id.isEmpty())stringResource(id = R.string.add_company_dialog_title)
        else stringResource(id = R.string.edit_company_dialog_title),
        onDismiss = onDismiss,
        onConfirm = {viewModel.onSaveData(popUp)})
    {
        Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween) {
            EditTextInDialog(
                value = companyEntity.name,
                onValueChange = viewModel::onNameChange,
                stringResource(id = R.string.add_company_dialog_name)
            )
            EditTextInDialog(
                value = companyEntity.website,
                onValueChange = viewModel::onWebSiteChange,
                stringResource(id = R.string.add_company_dialog_website)
            )
            EditTextInDialog(
                value = companyEntity.address,
                onValueChange = viewModel::onAddressChange,
                stringResource(id = R.string.add_company_dialog_address)
            )
            EditTextInDialog(
                value = companyEntity.logo?:"",
                onValueChange = viewModel::onLogoChange,
                stringResource(id = R.string.add_company_dialog_logo)
            )
        }
    }
}