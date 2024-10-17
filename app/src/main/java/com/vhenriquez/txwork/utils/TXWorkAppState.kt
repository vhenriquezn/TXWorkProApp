package com.vhenriquez.txwork.utils

import android.content.res.Resources
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.common.snackbar.SnackbarMessage.Companion.toMessage
import com.vhenriquez.txwork.navigation.Drawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class TXWorkAppState(
    val snackbarHostState: SnackbarHostState,
    val navController: NavHostController,
    val navControllerDrawer: NavHostController,
    val drawerState: DrawerState,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    private val coroutineScope: CoroutineScope,
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
                val text = snackbarMessage.toMessage(resources)
                snackbarHostState.showSnackbar(text)
                snackbarManager.clearSnackbarState()
            }
        }
    }

    fun navigateToActivities(companyIdSelected: String) {
        navControllerDrawer.navigate(Drawer.Activities(companyIdSelected)) {
            popUpTo(Drawer.Activities(companyIdSelected))
        }
    }

    fun navigateTo(route: Any) {
        navControllerDrawer.navigate(route) {
            launchSingleTop = true
            restoreState = true}
    }

    fun openDrawer() {
        coroutineScope.launch { drawerState.open() }
    }

    fun closeDrawer() {
        coroutineScope.launch { drawerState.close() }
    }

    fun popUp() {
        navController.popBackStack()
    }

    fun navigate(route: Any) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateAndPopUp(route: Any, popUp: Any) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: Any) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }

    fun resetNavDrawer() {
        navControllerDrawer.navigate(Drawer) {
            popUpTo(navControllerDrawer.graph.startDestinationId) {
                inclusive = true
            }
        }
        closeDrawer()
    }
}