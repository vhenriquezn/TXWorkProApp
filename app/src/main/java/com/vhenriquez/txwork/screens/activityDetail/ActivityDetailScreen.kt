package com.vhenriquez.txwork.screens.activityDetail

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.camera.CameraView
import com.vhenriquez.txwork.common.composable.DeleteDialog
import com.vhenriquez.txwork.common.composable.SearchTextField
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.ImageEntity
import com.vhenriquez.txwork.model.InstrumentEntity
import com.vhenriquez.txwork.navigation.Main
import com.vhenriquez.txwork.screens.instruments.InstrumentItem
import com.vhenriquez.txwork.screens.movements.MovementsTabs
import com.vhenriquez.txwork.screens.storage.createImageFile
import java.util.Objects

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun ActivityDetailScreen(
    viewModel: InstrumentsDetailViewModel = hiltViewModel(),
    openScreen: (Any) -> Unit,
    onNavigateBack: () -> Unit,
    ) {

    ContentActivityDetailScreen(
        viewModel = viewModel,
        openScreen = { route -> openScreen(route)},
        onNavigateBack = onNavigateBack)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContentActivityDetailScreen(
    viewModel: InstrumentsDetailViewModel,
    openScreen: (Any) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<InstrumentEntity>()
    val isDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val launcherGallery = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri?->
        selectedImageUri.value = uri
    }
    val uiState by viewModel.uiState
    val searchText by viewModel.searchText.collectAsState()
    val companyAppId by viewModel.companyAppIdSelected
    val selectedActivity by viewModel.selectedActivity.collectAsState()
    val instruments by viewModel.instruments.collectAsState()
    val context = LocalContext.current

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                openScreen(Main.AddInstrumentsToActivity(
                                    activityId = selectedActivity.id,
                                    businessId = selectedActivity.businessId,
                                    companyAppId = companyAppId))
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Instruments")
                        }
                    },
                    topBar = {
                        TopAppBar(title = {
                            Column {
                                Text(
                        text = selectedActivity.name,
                        fontSize = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = instruments.size.toString(),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                } },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = {
                        if (navigator.canNavigateBack())
                            navigator.navigateBack()
                        else
                            onNavigateBack()
                                         },
                        content = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, contentDescription = null
                        )

                    })
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                actions = {
                    IconButton(onClick = { viewModel.uiState.value = uiState.copy(showOptionsMenu = true) }) {
                        Icon(Icons.Default.MoreVert , contentDescription = "options")
                    }
                    DropdownMenu(
                        expanded = uiState.showOptionsMenu,
                        onDismissRequest = {viewModel.uiState.value = uiState.copy(showOptionsMenu = false)}) {
                        DropdownMenuItem(
                            onClick = {openScreen(Main.AddUsersToActivity(activityId = selectedActivity.id, companyAppId))},//addUsers
                            text = {
                                Row {
                                    Icon(imageVector = Icons.Filled.People, contentDescription = "users")
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = "Usuarios")
                                }
                            })
//                        DropdownMenuItem(
//                            onClick = {},
//                            text = {
//                                Row {
//                                    Icon(imageVector = Icons.Filled.ImageSearch, contentDescription = "Gallery")
//                                    Spacer(modifier = Modifier.width(10.dp))
//                                    Text(text = "Galería")
//                                }
//                            })

                    }
                }
            )
        }, modifier = Modifier
    ) {contentPadding->
                    MyInstrumentsList(
                        instruments = instruments,
                        selectionState = if (isDetailVisible && uiState.currentSelectedIndex >= 0) {
                            SelectionVisibilityState.ShowSelection(uiState.currentSelectedIndex)
                        } else {
                            SelectionVisibilityState.NoSelection
                        },
                        paddingValues = contentPadding,
                        searchText = searchText,
                        onSearchTextChange = viewModel::onSearchTextChange,
                        onItemClick = {index, instrument ->
                            viewModel.uiState.value = uiState.copy(currentSelectedIndex = index)
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, instrument )
                    },
                        onActionClick = {a,b,c -> viewModel.onInstrumentActionClick(a,b,c)},
                        openScreen = {route -> openScreen(route)})
                }
            }

        },
        detailPane = {
            AnimatedPane {
                navigator.currentDestination?.content.let{

                    MyInstrumentDetail(
                        selectedInstrument = it,
                        uiState = uiState,
                        generateReport =  {viewModel.generateReport(context)},
                        onShowTakePicture = {isVisible-> viewModel.uiState.value = uiState.copy(takePicture = isVisible ) },
                        uploadImage = viewModel::uploadImage,
                        onCameraClick = {
                            viewModel.uiState.value = uiState.copy(showAttachMenu = false)
                            viewModel.uiState.value = uiState.copy(takePicture = true )
                                        },
                        onGalleryClick = {
                            viewModel.uiState.value = uiState.copy(showAttachMenu = false)
                            launcherGallery.launch(PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        onShowAttachMenu = {isVisible-> viewModel.uiState.value = uiState.copy(showAttachMenu = isVisible) }
                    )
                }
            }
        }
    )

    AnimatedVisibility(visible = uiState.showDeleteDialog) {
        DeleteDialog(
            title = stringResource(id = R.string.delete_instrument_dialog_title),
                message = stringResource(id = R.string.delete_instrument_dialog_msg),
                onConfirmDelete = {viewModel.deleteInstrumentToActivity()},
                onDismiss = {viewModel.uiState.value = uiState.copy(showDeleteDialog = false)})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInstrumentDetail(
    uiState: ActivityDetailUiState,
    selectedInstrument: InstrumentEntity?,
    onShowTakePicture: (Boolean) -> Unit,
    generateReport: ()-> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    uploadImage: (Uri, InstrumentEntity) -> Unit,
    onShowAttachMenu: (Boolean)-> Unit) {

    Box(
        Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        if (selectedInstrument == null){
            Text(text = stringResource(id = R.string.no_instrument_selected),
                style = MaterialTheme.typography.titleLarge,
            )
        }else{
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        Text(
                            text = selectedInstrument.tag,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) },
                        modifier = Modifier.fillMaxWidth(),
                        navigationIcon = {
//                                IconButton(onClick = {navigation.popBackStack()},
//                                    content = {
//                                        Icon(
//                                            imageVector = Icons.Default.ArrowBack, contentDescription = null
//                                        )
//                                    })
                        },
                        actions = {
                            Row {
                                IconButton(onClick = { generateReport() }) {
                                    Icon(Icons.Filled.Description , contentDescription = "certificate")
                                }
                                IconButton(onClick = { onShowAttachMenu(true) }) {
                                    Icon(Icons.Filled.AttachFile , contentDescription = "attach")
                                }
                                DropdownMenu(
                                    expanded = uiState.showAttachMenu,
                                    onDismissRequest = {onShowAttachMenu(false)}) {
                                    DropdownMenuItem(
                                        onClick = onCameraClick,
                                        text = {
                                            Row {
                                                Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "Camara")
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(text = "Camara")
                                            }
                                        })
                                    DropdownMenuItem(
                                        onClick = onGalleryClick,
                                        text = {
                                            Row {
                                                Icon(imageVector = Icons.Filled.ImageSearch, contentDescription = "Gallery")
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(text = "Galería")
                                            }
                                        })

                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),

                        )
                },
                modifier = Modifier
            ) {contentPadding->
                Box(
                    Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    MovementsTabs(contentPadding, selectedInstrument.id)
                }
            }
        }
        AnimatedVisibility(visible = uiState.takePicture) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                CameraView(
                    onImageCaptured = { uri ->
                        uri?.let { selectedInstrument?.let { it1 -> uploadImage(it, it1) } }
                        onShowTakePicture(false)
                    }
                )
            }

        }
    }
}

@Composable
fun MyInstrumentsList(
    instruments: List<InstrumentEntity>,
    selectionState : SelectionVisibilityState,
    paddingValues: PaddingValues,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onItemClick: (Int, InstrumentEntity) -> Unit,
    onActionClick: ((Any) -> Unit, Int, InstrumentEntity) -> Unit,
    openScreen: (Any) -> Unit) {

    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .then(
            when (selectionState) {
                SelectionVisibilityState.NoSelection -> Modifier
                is SelectionVisibilityState.ShowSelection -> Modifier.selectableGroup()
            }
        )){
        item {
            SearchTextField(searchText, onSearchTextChange)
        }
        itemsIndexed(instruments, key = {_, item ->  item.id
        }) {index, instrument ->

            val containerColor = when (selectionState) {
                SelectionVisibilityState.NoSelection -> null
                is SelectionVisibilityState.ShowSelection ->
                    if (index == selectionState.indexSelected) {
                        CardDefaults.cardColors().disabledContentColor
                    } else {
                        null
                    }
            }

            InstrumentItem(
                containerColor = containerColor,
                instrument = instrument,
                onClick = {onItemClick(index,it)},
                onActionClick = {actionIndex-> onActionClick(openScreen, actionIndex, instrument)}
            )
        }

    }
}

sealed interface SelectionVisibilityState {
    data object NoSelection : SelectionVisibilityState
    data class ShowSelection(
        val indexSelected: Int
    ) : SelectionVisibilityState
}
