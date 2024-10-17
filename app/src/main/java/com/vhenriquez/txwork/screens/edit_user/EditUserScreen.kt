package com.vhenriquez.txwork.screens.edit_user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.CustomDatePickerDialog
import com.vhenriquez.txwork.common.composable.DropdownSelector
import com.vhenriquez.txwork.common.composable.EditTextInDialog
import com.vhenriquez.txwork.common.composable.PopupBox
import com.vhenriquez.txwork.model.CertificateEntity
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.screens.edit_certificate.EditCertificateUiState
import com.vhenriquez.txwork.screens.login.LoginScreenContent
import com.vhenriquez.txwork.screens.login.LoginUiState
import com.vhenriquez.txwork.ui.theme.TXWorkTheme

@Composable
fun EditUserScreen(
    popUp: () -> Unit,
    viewModel: EditUserViewModel = hiltViewModel()){

    EditUserScreenContent(
        viewModel = viewModel,
        popUp = popUp,
    )
}

@Composable
fun EditUserScreenContent(
    popUp: () -> Unit,
    viewModel: EditUserViewModel
){
    val user by viewModel.user
    val uiState by viewModel.uiState

    PopupBox(
        title = if (user.id.isEmpty()) stringResource(id = R.string.add_user_dialog_title)
        else stringResource(id = R.string.edit_user_dialog_title),
        onDismiss = popUp,
        onConfirm = {viewModel.onSaveData(popUp)})
    {
        Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween) {
            EditTextInDialog(
                value = user.userName,
                onValueChange = viewModel::onNameChange,
                label = stringResource(id = R.string.add_user_dialog_name)
            )
            EditTextInDialog(
                value = user.email ?:"",
                onValueChange = viewModel::onEmailChange,
                label = stringResource(id = R.string.add_user_dialog_email)
            )
            DropdownSelector(
                label = R.string.add_user_dialog_userType,
                options = stringArrayResource(id = R.array.user_type_options).asList(),
                selection = user.userType,
                modifier = Modifier
            ) { item, position ->
                viewModel.onUserTypeChange(item)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(id = R.string.add_user_permissions_title),
                Modifier.align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Instrumentos")
                Switch(checked = user.roles["instruments"]?:false, onCheckedChange = viewModel::onInstrumentChange)
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Empresas")
                Switch(checked = user.roles["business"]?:false, onCheckedChange = viewModel::onBusinessChange)
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Users")
                Switch(checked = user.roles["users"]?:false, onCheckedChange = viewModel::onUsersChange)
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Patrones")
                Switch(checked = user.roles["patterns"]?:false, onCheckedChange = viewModel::onPatternsChange)
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Herramientas")
                Switch(checked = user.roles["tools"]?:false, onCheckedChange = viewModel::onToolsChange)
            }
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true, )
@Composable
fun UserScreenPreview() {
    val viewModel: EditUserViewModel = hiltViewModel()
    val uiState = EditUserUiState(
    )
    TXWorkTheme {
        EditUserScreenContent(
            viewModel = viewModel,
            popUp = {}
        )
    }
}