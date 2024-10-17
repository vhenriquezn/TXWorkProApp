package com.vhenriquez.txwork.screens.companies

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.DeleteDialog
import com.vhenriquez.txwork.common.composable.SearchTextField
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.navigation.Main
import com.vhenriquez.txwork.screens.activities.ActivityItem

@Composable
fun CompaniesScreen(viewModel: CompaniesViewModel = hiltViewModel(),
                    openScreen: (Any) -> Unit) {

    CompaniesScreenContent(
        viewModel = viewModel,
        openScreen = { route -> openScreen(route)}
    )
}

@Composable
fun CompaniesScreenContent(
    viewModel: CompaniesViewModel,
    openScreen: (Any) -> Unit) {

    val uiState by viewModel.uiState
    val companies by viewModel.companies.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val companyAppIdSelected by viewModel.companyAppIdSelected
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation
    val itemCount = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 2
        else -> 1
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    openScreen(Main.EditCompany(companyAppId = companyAppIdSelected))
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Company")
            }
        }
    ) { contentPadding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(itemCount),
            modifier = Modifier.fillMaxSize().padding(contentPadding)
        ) {
            item {
                SearchTextField(searchText, viewModel::onSearchTextChange)
            }
            items(companies, key = { it.id }) { company ->
                CompanyItem(
                    company = company,
                    onClick = { openScreen(Main.EditCompany(company.id, companyAppIdSelected)) },
                    onActionClick = { actionIndex ->
                        viewModel.onCompanyActionClick(actionIndex, company)
                    }
                )
            }

            if (companies.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.height(450.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.no_companies),
                            fontSize = 18.sp, fontWeight = FontWeight.Thin,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = uiState.showDeleteCompanyDialog) {
            DeleteDialog(
                title = stringResource(id = R.string.delete_company_dialog_title),
                message = stringResource(id = R.string.delete_company_dialog_msg),
                onConfirmDelete = {viewModel.deleteCompany()},
                onDismiss = {viewModel.uiState.value = uiState.copy(showDeleteCompanyDialog = false)})
        }
    }
}
