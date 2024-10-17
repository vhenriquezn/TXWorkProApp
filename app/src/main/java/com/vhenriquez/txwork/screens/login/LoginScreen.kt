package com.vhenriquez.txwork.screens.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.AuthLogo
import com.vhenriquez.txwork.common.composable.effects.AppCircularProgressBar
import com.vhenriquez.txwork.common.composable.effects.ProgressType
import com.vhenriquez.txwork.ui.theme.Purple40
import com.vhenriquez.txwork.ui.theme.TXWorkTheme

@Composable
fun LoginScreen(
    navigateToMain: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    LoginScreenContent(
        uiState = uiState,
        viewModel = viewModel,
        navigateToMain = navigateToMain,
        navigateToSignUp = navigateToSignUp,
        onForgotPasswordClick = { navigateToForgotPassword() },
        onNavigateBack = {onNavigateBack()}
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    viewModel: LoginViewModel,
    navigateToMain: () -> Unit,
    navigateToSignUp: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onNavigateBack: () -> Unit
) {

    BackHandler(enabled = true) {
        onNavigateBack()
    }
    if (booleanResource(id = R.bool.large_layout)){
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            AuthLogo(stringResource(id = R.string.login_title))
            ContentLogin(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onSignInClick = {viewModel.onSignInClick(navigateToMain)},
                onSignUpClick = navigateToSignUp,
                onSignInGoogle = {  },
                onForgotPasswordClick = onForgotPasswordClick,
                isLoginProgress = uiState.isLoading,
                onIsPasswordVisible = {viewModel.uiState.value = viewModel.uiState.value.copy(isPasswordVisible = it)}
            )
        }
    }else{
        ContentLogin(
            uiState = uiState,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onSignInClick = {viewModel.onSignInClick(navigateToMain)},
            onSignUpClick = navigateToSignUp,
            onSignInGoogle = {  },
            onForgotPasswordClick = onForgotPasswordClick,
            isLoginProgress = uiState.isLoading,
            onIsPasswordVisible = {viewModel.uiState.value = viewModel.uiState.value.copy(isPasswordVisible = it)}
        )
    }
}

@Composable
fun ContentLogin(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onSignInGoogle: () -> Unit,
    onIsPasswordVisible: (Boolean) -> Unit,
    onForgotPasswordClick: () -> Unit,
    isLoginProgress: Boolean
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!booleanResource(id = R.bool.large_layout)){
            AuthLogo(stringResource(id = R.string.login_title))
        }
        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Email") },
                value = uiState.email,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = { onEmailChange(it)},
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "")
                })
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Contraseña") },
                value = uiState.password,
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { onPasswordChange(it) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Key,
                        contentDescription = "")
                },
                trailingIcon = {
                    IconButton(onClick = {onIsPasswordVisible(!uiState.isPasswordVisible) }) {
                        Icon(
                            if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "")
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {onSignInClick()},
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Box {
                    if (isLoginProgress) {
                        AppCircularProgressBar(progressType = ProgressType.SMALL)
                    } else {
                        Text(text = "Iniciar Sesión".uppercase())

                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ClickableText(
            text = AnnotatedString("¿Olvidaste tu contraseña?"),
            onClick = {onForgotPasswordClick()},
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple40
            )
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(text = "-------- o --------", style = TextStyle(color = Color.Gray))
        Spacer(modifier = Modifier.height(25.dp))

        SocialMediaButton(
            onClick = {
                onSignInGoogle()

                //viewModel?.signInWithGoogle(googleSignInLauncher)
                //viewModel.signInWithGoogle(googleSignInLauncher)
                //auth.signInWithGoogle(googleSignInLauncher)
            },
            text = "Continuar con Google",
            icon = R.drawable.ic_google,
            color = Color(0xFFF1F1F1)
        )
        Spacer(modifier = Modifier.height(25.dp))

        ClickableText(
            text = AnnotatedString("¿No tienes una cuenta? Regístrate"),
            onClick = {onSignUpClick()},
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple40
            )
        )
    }
}

@Composable
fun SocialMediaButton(onClick: () -> Unit, text: String, icon: Int, color: Color, ) {
    var click by remember { mutableStateOf(false) }
    Surface(
        onClick = onClick,
        modifier = Modifier
            .padding(start = 40.dp, end = 40.dp)
            .clickable { click = !click },
        shape = RoundedCornerShape(50),
        border = BorderStroke(width = 1.dp, color = Color.Gray),
        color = color
    ) {
        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                modifier = Modifier.size(24.dp),
                contentDescription = text,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.Black)
            click = true
        }
    }
}

@Preview(showBackground = true, )
@Composable
fun LoginScreenPreview() {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState = LoginUiState(
        email = "email@test.com"
    )
    TXWorkTheme {
        LoginScreenContent(
            uiState = uiState,
            viewModel = viewModel,
            navigateToMain = {},
            navigateToSignUp = {},
            onForgotPasswordClick = { },
            onNavigateBack = {}
        )
    }
}