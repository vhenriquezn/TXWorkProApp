package com.vhenriquez.txwork.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.ui.theme.TXWorkTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateToLogin: () -> Unit,
    navigateToMain: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    SplashScreenContent(
        onAppStart = {
            viewModel.onAppStart(navigateToMain, navigateToLogin)
        }
    )
}
@Composable
fun SplashScreenContent(
    modifier: Modifier = Modifier,
    onAppStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.text_splash),
            style = TextStyle(fontSize = 30.sp))
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_tx_work),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )
    }
    LaunchedEffect(true) { onAppStart() }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    TXWorkTheme {
        SplashScreenContent(
            onAppStart = {}
        )
    }
}