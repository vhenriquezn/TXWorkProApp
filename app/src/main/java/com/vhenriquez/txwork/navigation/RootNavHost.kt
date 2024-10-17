package com.vhenriquez.txwork.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.vhenriquez.txwork.utils.TXWorkAppState

@Composable
fun RootNavHost(appState: TXWorkAppState) {
    NavHost(
        navController = appState.navController,
        startDestination = Auth
    ) {
        authNavGraph(appState)
        mainNavGraph(appState)
    }
}