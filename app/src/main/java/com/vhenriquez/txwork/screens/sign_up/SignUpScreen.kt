package com.vhenriquez.txwork.screens.sign_up

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
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.AuthLogo
import com.vhenriquez.txwork.common.composable.effects.AppCircularProgressBar
import com.vhenriquez.txwork.common.composable.effects.ProgressType
import com.vhenriquez.txwork.screens.login.ContentLogin
import com.vhenriquez.txwork.ui.theme.Purple40
import com.vhenriquez.txwork.ui.theme.TXWorkTheme

@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    SignUpScreenContent(
        uiState = uiState,
        viewModel = viewModel,
        onNavigateBack ={onNavigateBack() }
    )
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState,
    viewModel: SignUpViewModel,
    onNavigateBack: () -> Unit
) {
    if (booleanResource(id = R.bool.large_layout)){
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            AuthLogo(stringResource(id = R.string.signup_title))
            ContentSignUp(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onRepeatPasswordChange = viewModel::onRepeatPasswordChange,
                onSignUpClick ={viewModel.onSignUpClick(onNavigateBack)},
                onNavigateBack = onNavigateBack,
                isLoginProgress = uiState.isLoading
            )
        }
    }else{
        ContentSignUp(
            uiState = uiState,
            onNameChange = viewModel::onNameChange,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onRepeatPasswordChange = viewModel::onRepeatPasswordChange,
            onSignUpClick = {viewModel.onSignUpClick(onNavigateBack)},
            onNavigateBack = onNavigateBack,
            isLoginProgress = uiState.isLoading
        )
    }
}

@Composable
fun ContentSignUp(
    uiState: SignUpUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateBack: () -> Unit,
    isLoginProgress: Boolean
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!booleanResource(id = R.bool.large_layout)){
            AuthLogo(stringResource(id = R.string.signup_title))
        }
        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Nombre y Apellido") },
                value = uiState.name,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {onNameChange(it)})
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Correo electrónico") },
                value = uiState.email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = {onEmailChange(it)})
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Contraseña") },
                value = uiState.password,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = {onPasswordChange(it)})
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Repetir Contraseña") },
                value = uiState.repeatPassword,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = {onRepeatPasswordChange(it)})
        }
        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    onSignUpClick()
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Box {
                    if (isLoginProgress) {
                        AppCircularProgressBar(progressType = ProgressType.SMALL)
                    } else {
                        Text(text = "Registrarse".uppercase())

                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        ClickableText(
            text = AnnotatedString("¿Ya tienes cuenta? Inicia sesión"),
            onClick = {onNavigateBack()},
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple40
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val viewModel: SignUpViewModel = hiltViewModel()
    val uiState = SignUpUiState(
        name = "Name Test",
        email = "email@test.com"
    )
    TXWorkTheme {
        SignUpScreenContent(
            uiState = uiState,
            viewModel = viewModel,
            onNavigateBack = { }
        )
    }
}