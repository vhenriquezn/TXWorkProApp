package com.vhenriquez.txwork.screens.movements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.ItemsTabsMovements
import com.vhenriquez.txwork.model.ItemsTabsMovements.*
import kotlinx.coroutines.launch

@Composable
fun MovementsTabs(paddingValues: PaddingValues, instrumentId: String,
                  viewModel: DetailInstrumentViewModel = hiltViewModel()) {
    viewModel.getSelectedInstrument(instrumentId)
    val tabs = listOf(
        TabInformation(stringResource(R.string.title_tab_information),viewModel),
        TabCalibration(stringResource(R.string.title_tab_calibration),viewModel))
    val pagerState = rememberPagerState{tabs.size}
    Column(modifier = Modifier.padding(paddingValues)
    ) {
        Tabs(tabs, pagerState)
        TabsContent(tabs, pagerState)
    }
}

@Composable
fun TabsContent(
    tabs: List<ItemsTabsMovements>,
    pagerState: PagerState) {
    HorizontalPager(state = pagerState) {page->
       tabs[page].screen()
    }
}

@Composable
fun Tabs(tabs: List<ItemsTabsMovements>, pagerState: PagerState){
    val selectedTab = pagerState.currentPage
    val scope = rememberCoroutineScope()
    TabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, items ->
            Tab(
                selected = selectedTab == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = { Text(text = items . title)},
            )
        }
    }
}
