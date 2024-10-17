package com.vhenriquez.txwork.screens.activityDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.PopupBox
import com.vhenriquez.txwork.common.composable.SearchTextField
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.navigation.Main

@Composable
fun AddInstrumentsToActivityScreen(
    popUp: () -> Unit,
    openScreen: (Any) -> Unit,
    viewModel: AddInstrumentsToActivityViewModel = hiltViewModel()){

    val activityId = viewModel.activityId
    val companyAppIdSelected = viewModel.companyAppIdSelected
    val instruments by viewModel.instruments.collectAsState()
    val searchText by viewModel.searchText.collectAsState()

    AddInstrumentsToActivityScreenContent(
        instruments = instruments,
        activityId = activityId?: "",
        onDismiss = {popUp()},
        searchText = searchText,
        onSearchTextChange = viewModel::onSearchTextChange,
        onFabClick = {openScreen(Main.EditInstrument(
            activityId = activityId,
            companyAppId = companyAppIdSelected.value))},
        onAddClick = { viewModel.addInstrumentToActivity(it) },
    )
}

@Composable
fun AddInstrumentsToActivityScreenContent(
    instruments: List<InstrumentEntity>,
    activityId: String,
    onAddClick: (instrument:InstrumentEntity) -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
){

    PopupBox(
        title = stringResource(id = R.string.add_instruments_dialog_title),
        isFullScreen = !booleanResource(id = R.bool.large_layout),
        onDismiss = onDismiss,
        onConfirm = onDismiss,
        fabVisibility = true,
        onFabClick = onFabClick )
    {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(1),
            modifier = Modifier.fillMaxSize()) {
            item {
                SearchTextField(searchText, onSearchTextChange)
            }
            items(instruments, key = { it.id }) { instrument ->
                AddInstrumentItem(
                    instrument = instrument,
                    activityId = activityId,
                    onAddClick = {onAddClick(instrument)},
                )
            }
        }
    }
}

@Composable
fun AddInstrumentItem(
    instrument: InstrumentEntity,
    activityId: String,
    onAddClick: () -> Unit,
) {
    val status = instrument.activities.contains(activityId)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 2.dp, 0.dp, 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .weight(0.6f)
            ) {
                Text(
                    text = instrument.tag,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,)

                Text(
                    text = instrument.area,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,)
            }

            Button(
                modifier = Modifier.weight(0.4f),
                onClick =  onAddClick){
                Text(text = if (status) stringResource(R.string.btn_txt_remove) else stringResource(R.string.btn_txt_add) )
            }
        }


    }
}