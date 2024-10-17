package com.vhenriquez.txwork.screens.certificates

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.navigation.Main

@Composable
fun CertificatesScreen(viewModel: CertificatesViewModel = hiltViewModel(),
                       openScreen: (Any) -> Unit) {

    CertificatesScreenContent(
        viewModel = viewModel,
        openScreen = { route -> openScreen(route)}
    )
}

@Composable
fun CertificatesScreenContent(
    viewModel: CertificatesViewModel,
    openScreen: (Any) -> Unit){

    val uiState by viewModel.uiState
    val certificates by viewModel.certificates.collectAsState()
    val companyAppIdSelected by viewModel.companyAppIdSelected
    val searchText by viewModel.searchText.collectAsState()
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
                    openScreen(Main.EditCertificate(companyAppId = companyAppIdSelected))
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Certificate")
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
            items(certificates.size) { index ->
                val certificate = certificates[index]
                CertificateItem(
                    certificate = certificate,
                    onClick = { openScreen(Main.EditCertificate(certificate.id, companyAppIdSelected)) },
                    onActionClick = { actionIndex ->
                        viewModel.onCertificateActionClick(actionIndex, certificate)
                    }
                )
            }
            if (certificates.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.height(450.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.no_certificates),
                            fontSize = 18.sp, fontWeight = FontWeight.Thin,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = uiState.showDeleteCertificateDialog) {
            DeleteDialog(
                title = stringResource(id = R.string.delete_certificate_dialog_title),
                message = stringResource(id = R.string.delete_certificate_dialog_msg),
                onConfirmDelete = { viewModel.deleteCertificate() },
                onDismiss = {viewModel.uiState.value = uiState.copy(showDeleteCertificateDialog = false)})
        }
    }
}
