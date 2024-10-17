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
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.navigation.Main

@Composable
fun AddUsersToActivityScreen(
    popUp: () -> Unit,
    openScreen: (Any) -> Unit,
    viewModel: AddUsersToActivityViewModel = hiltViewModel()){

    val activity = viewModel.activitySelected.collectAsState()
    val users by viewModel.users.collectAsState()
    val searchText by viewModel.searchText.collectAsState()

    AddUsersToActivityScreenContent(
        users = users,
        activity = activity.value,
        onDismiss = {popUp()},
        searchText = searchText,
        onSearchTextChange = viewModel::onSearchTextChange,
        onFabClick = {
//            openScreen(Main.EditInstrument(
//            activityId = activityId,
//            companyAppId = companyAppIdSelected.value))
                     },
        onAddClick = viewModel::addUserToActivity,
    )
}

@Composable
fun AddUsersToActivityScreenContent(
    users: List<UserEntity>,
    activity: ActivityEntity,
    onAddClick: (UserEntity, Boolean) -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
){

    PopupBox(
        title = stringResource(id = R.string.dialog_add_users_to_activity_title),
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
            items(users, key = { it.id }) { user ->
                AddUserItem(
                    user = user,
                    activity = activity,
                    onAddClick = onAddClick)
            }
        }
    }
}

@Composable
fun AddUserItem(
    user: UserEntity,
    activity: ActivityEntity,
    onAddClick: (UserEntity, Boolean) -> Unit,
) {
    val status = activity.users.contains(user.id)
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
                    text = user.userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,)

                Text(
                    text = user.email?:"",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,)
            }

            Button(
                modifier = Modifier.weight(0.4f),
                onClick =  {onAddClick(user, status)}){
                Text(text = if (status) stringResource(R.string.btn_txt_remove) else stringResource(R.string.btn_txt_add) )
            }
        }


    }
}