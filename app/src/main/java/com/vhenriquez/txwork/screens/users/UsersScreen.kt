package com.vhenriquez.txwork.screens.users

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.SearchTextField
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.navigation.Main
import com.vhenriquez.txwork.screens.patterns.PatternItem

@Composable
fun UsersScreen(viewModel: UsersViewModel = hiltViewModel(),
                openScreen: (Any) -> Unit) {

    UsersScreenContent(
        viewModel= viewModel,
        openScreen = { route -> openScreen(route) }
    )
}

@Composable
fun UsersScreenContent(
    viewModel: UsersViewModel,
    openScreen: (Any) -> Unit
){
    val uiState by viewModel.uiState
    val users by viewModel.users.collectAsState()
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
                    openScreen(Main.EditUser(companyAppId = companyAppIdSelected))
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

            items(users, key = {it.id}) { user ->
                UserItem(
                    user = user,
                    onClick = {openScreen(Main.EditUser(user.id, companyAppIdSelected)) },
                    onActionClick = {actionIndex-> viewModel.onUserActionClick(openScreen, actionIndex, user) }
                )
            }
            if (users.isEmpty()){
                item {
                    Column(
                        modifier = Modifier.height(450.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.no_peoples),
                            fontSize = 18.sp, fontWeight = FontWeight.Thin,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}