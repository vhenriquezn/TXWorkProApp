package com.vhenriquez.txwork

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.navigation.RootNavHost
import com.vhenriquez.txwork.ui.theme.TXWorkTheme
import com.vhenriquez.txwork.utils.PermissionRequester
import com.vhenriquez.txwork.utils.TXWorkAppState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        setContent {
            TXWorkTheme {
                Surface (color = MaterialTheme.colorScheme.background){
                    val appState = rememberAppState()
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(
                                hostState = appState.snackbarHostState,
                                modifier = Modifier.padding(8.dp),
                                snackbar = {snackbarData ->
                                    Snackbar(snackbarData, contentColor = MaterialTheme.colorScheme.onPrimary)
                                })},

                        ) { paddingValues ->
                        Box(
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            RootNavHost(appState)
                        }
                    }
                }
            }
        }
    }



    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissionLauncher = PermissionRequester(this, android.Manifest.permission.POST_NOTIFICATIONS,
                    onRationale = {},
                    onDenied = {})
            }
        }
    }
    private var requestPermissionLauncher: PermissionRequester? = null

    override fun onDestroy() {
        super.onDestroy()
    }

}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController(),
    navControllerDrawer: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(snackbarHostState, navController, navControllerDrawer, drawerState, snackbarManager, resources, coroutineScope) {
        TXWorkAppState(snackbarHostState, navController, navControllerDrawer,drawerState, snackbarManager, resources, coroutineScope)
    }

