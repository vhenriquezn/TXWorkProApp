package com.vhenriquez.txwork.screens.forgotPassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.AuthLogo
import com.vhenriquez.txwork.screens.login.ContentLogin
import com.vhenriquez.txwork.ui.theme.TXWorkTheme

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val email by viewModel.email

    ForgotPasswordScreenContent(
        email = email,
        onEmailChange = viewModel::onEmailChange,
        onSendPasswordResetEmailClick = { viewModel.onSendPasswordResetEmailClick(onNavigateBack) },
    )
}

@Composable
fun ForgotPasswordScreenContent(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    onSendPasswordResetEmailClick: () -> Unit
) {
    if (booleanResource(id = R.bool.large_layout)){
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            AuthLogo(stringResource(id = R.string.forgot_password_title))
            ContentForgotPassword(
                email = email,
                onEmailChange = onEmailChange,
                onSendPasswordResetEmailClick = onSendPasswordResetEmailClick
            )
        }
    }else{
        ContentForgotPassword(
            email = email,
            onEmailChange = onEmailChange,
            onSendPasswordResetEmailClick = onSendPasswordResetEmailClick
        )
    }
}

@Composable
fun ContentForgotPassword(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendPasswordResetEmailClick: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!booleanResource(id = R.bool.large_layout)){
            AuthLogo(stringResource(id = R.string.forgot_password_title))
        }
        Text(
            modifier = Modifier.padding(40.dp, 20.dp, 40.dp, 0.dp),
            text = stringResource(id = R.string.resetPassMessage),
            style = TextStyle(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(50.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Correo electrónico") },
                value = email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = { onEmailChange(it) })
        }

        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {onSendPasswordResetEmailClick()},
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Recuperar contraseña")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    TXWorkTheme {
        ForgotPasswordScreenContent(
            email = "email@test.com",
            onEmailChange = { },
            onSendPasswordResetEmailClick = { }
        )
    }
}