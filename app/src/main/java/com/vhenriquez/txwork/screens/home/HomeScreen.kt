package com.vhenriquez.txwork.screens.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.common.composable.PopupBox
import com.vhenriquez.txwork.navigation.Drawer
import com.vhenriquez.txwork.navigation.drawerNavGraph
import com.vhenriquez.txwork.screens.AppDrawer
import com.vhenriquez.txwork.utils.TXWorkAppState

private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
private var welcomeMessage by mutableStateOf("Bienvenidx")
private var isButtonVisible by mutableStateOf(true)

val WELCOME_MESSAGE_KEY = "welcome_message"
val IS_BUTTON_VISIBLE_KEY = "is_button_visible"

@Composable
fun HomeScreen(
    appState: TXWorkAppState,
    restartApp: (Any) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeScreenContent(
        appState = appState,
        viewModel = viewModel,
        restartApp = restartApp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    appState: TXWorkAppState,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    restartApp: (Any) -> Unit,
) {
    initRemoteConfig()
    val currentRoute = appState.navControllerDrawer.currentBackStackEntryAsState().value?.destination?.route ?: Drawer.Activities.toString()
    val uiState by viewModel.uiState
    val companyIdSelected by viewModel.companyId.collectAsState("")
    val user by viewModel.user.collectAsState()
    val companies by viewModel.companies.collectAsState()
    val invitations by viewModel.invitations.collectAsState()
    val startDestinationDrawer by viewModel.startDestinationDrawer

    ModalNavigationDrawer(drawerContent = {
        AppDrawer(
            companyIdSelected = companyIdSelected,
            user = user,
            route = currentRoute,
            companies = companies,
            navigateToActivities = { appState.navigateToActivities(companyIdSelected) },
            navigateTo = {appState.navigateTo(it)},
            openScreen = {appState.navigate(it)},
            closeDrawer =  {appState.closeDrawer()},
            onShowLogoutDialog = {viewModel.uiState.value = uiState.copy(showLogoutDialog = it)},
            onAppSettingClick = {  },
            modifier = Modifier,
            onTitleChange = viewModel::onTitleChange,
            updateSelectedCompany = {viewModel.updateSelectedCompany(it, appState)}
        )
    }, drawerState = appState.drawerState) {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Column {
                        Text(
                            text = uiState.title,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } },
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(
                            onClick = {appState.openDrawer() },
                            content = {
                            Icon(
                                imageVector = Icons.Default.Menu, contentDescription = null
                            )
                        })
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    actions = {
                        BadgedBox(
                            badge = {
                                if (invitations.isNotEmpty()) {
                                    Badge(
                                        containerColor = Color.Red,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = invitations.size.toString())
                                    }
                                }
                            }, modifier = Modifier.clickable {
                                viewModel.uiState.value = uiState.copy(showNotifications = true)
                            }
                        ) {  Icon(Icons.Default.Notifications , contentDescription = "notifications")}

                        IconButton(
                            onClick = {
                            },
                            modifier = Modifier.alpha(if (isButtonVisible) 1f else 0f)
                        ) {
                            Icon(Icons.Default.MoreVert , contentDescription = "options")
                        }
                    }
                )
            }, modifier = Modifier
        ) {contentPadding->
            startDestinationDrawer?.let {
                NavHost(
                    navController = appState.navControllerDrawer,
                    startDestination = Drawer,
                    modifier = Modifier.padding(contentPadding)
                ) {
                    drawerNavGraph(appState = appState, it)
                }
            }

            AnimatedVisibility(uiState.showLogoutDialog) {
                LogoutDialog(onConfirmLogout = {
                    viewModel.onSignOutClick(restartApp)
                },
                    onDismiss = { viewModel.uiState.value = uiState.copy(showLogoutDialog = false) })
            }
            AnimatedVisibility(uiState.showNotifications) {
                PopupBox(
                    title = stringResource(id = R.string.notifications_dialog_title),
                    onDismiss = {viewModel.uiState.value = uiState.copy(showNotifications = false)},
                    onConfirm = {viewModel.uiState.value = uiState.copy(showNotifications = false)} )
                {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(1),
                        modifier = Modifier.fillMaxWidth()) {

                        items(invitations, key = { it }) { invitation ->
                            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(text = invitation["companyName"].toString())
                                }
                                Row {
                                    IconButton(onClick = {
                                        viewModel.onAccept(invitation, false)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "decline"
                                        )
                                    }
                                    IconButton(onClick = {
                                        viewModel.onAccept(invitation, true)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "accept"
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}

fun initRemoteConfig() {
    mFirebaseRemoteConfig = Firebase.remoteConfig
    val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(3600)
        .build()
    mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

    mFirebaseRemoteConfig.addOnConfigUpdateListener(object: ConfigUpdateListener {
        override fun onUpdate(configUpdate: ConfigUpdate) {
            Log.d("HomeScreen", "Updated keys: " + configUpdate.updatedKeys)
            if(configUpdate.updatedKeys.contains(IS_BUTTON_VISIBLE_KEY) || configUpdate.updatedKeys.contains(
                    WELCOME_MESSAGE_KEY
                )) {
                mFirebaseRemoteConfig.activate().addOnCompleteListener {
                    displayWelcomeMessage()
                }
            }
        }
        override fun onError(error: FirebaseRemoteConfigException) {
        }
    })
    fetchWelcome()
}

fun fetchWelcome() {
    mFirebaseRemoteConfig.fetchAndActivate()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updated = task.result
                println("Parámetros actualizados: $updated")
            } else {
                println("Fetch failed")
            }
        }
}

fun displayWelcomeMessage() {
    welcomeMessage = mFirebaseRemoteConfig[WELCOME_MESSAGE_KEY].asString()
    isButtonVisible = mFirebaseRemoteConfig[IS_BUTTON_VISIBLE_KEY].asBoolean()
}

@Composable
fun LogoutDialog(onConfirmLogout: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cerrar sesión") },
        text = { Text("¿Estás seguro que deseas cerrar sesión?") },
        confirmButton = {
            Button(
                onClick = onConfirmLogout
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}
