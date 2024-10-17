package com.vhenriquez.txwork.screens.instruments

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.DeleteDialog
import com.vhenriquez.txwork.common.composable.SearchTextField
import com.vhenriquez.txwork.common.composable.TextSpannable
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.navigation.Main
import com.vhenriquez.txwork.screens.edit_instrument.EditInstrumentScreen
import com.vhenriquez.txwork.ui.theme.TXWorkTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InstrumentsScreen(
    viewModel: InstrumentsViewModel = hiltViewModel(),
    openScreen: (Any) -> Unit) {

    InstrumentsScreenContent(
        viewModel = viewModel,
        openScreen = { route -> openScreen(route)}
    )
}
@Composable
fun InstrumentsScreenContent(
    viewModel: InstrumentsViewModel,
    openScreen: (Any) -> Unit,
){
    val uiState by viewModel.uiState
    val instruments by viewModel.instruments.collectAsState()
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
                    openScreen(Main.EditInstrument(companyAppId = companyAppIdSelected))
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Instrument")
            }
        }
    ) {contentPadding->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(itemCount),
            modifier = Modifier.fillMaxSize().padding(contentPadding)) {
            item {
                SearchTextField(searchText, viewModel::onSearchTextChange)
            }
            items(instruments, key = { it.id }) { instrument ->
                InstrumentItem(
                    instrument = instrument,
                    onClick = {openScreen(Main.InstrumentDetail(instrument.id))},
                    onActionClick = {actionIndex-> viewModel.onInstrumentActionClick(openScreen, actionIndex, instrument)}
                )
            }
            if (instruments.isEmpty()){
                item {
                    Column(
                        modifier = Modifier.height(450.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.no_instruments),
                            fontSize = 18.sp, fontWeight = FontWeight.Thin,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
    AnimatedVisibility(visible = uiState.showDeleteInstrumentDialog) {
        DeleteDialog(
            title = stringResource(id = R.string.delete_instrument_dialog_title),
            message = stringResource(id = R.string.delete_instrument_dialog_msg),
            onConfirmDelete = {viewModel.deleteInstrument()},
            onDismiss = {viewModel.uiState.value = uiState.copy(showDeleteInstrumentDialog = false)})
    }
}



@Preview(showBackground = true )
@Composable
fun InstrumentsScreenPreview() {
    TXWorkTheme {
        InstrumentsScreen {

        }
    }
}
