package com.vhenriquez.txwork.camera

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material.icons.outlined.FlipCameraAndroid
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.vhenriquez.txwork.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CameraView(onImageCaptured: (Uri?) -> Unit, viewModel: CameraViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState
    var lensFacing by remember {mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture: ImageCapture = remember {ImageCapture.Builder().build()}
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.uiState.value = uiState.copy(imageUri = uri, previewVisible = uri != null)
    }

    CameraPreviewView(
        coroutineScope = coroutineScope,
        uiState = uiState,
        imageCapture = imageCapture,
        cameraPreviewUIAction = {cameraPreviewUIAction ->
            when(cameraPreviewUIAction){
                is CameraPreviewUIAction.OnCropOptions->{

                }
                is CameraPreviewUIAction.OnConfirmClick->{
                    onImageCaptured(uiState.imageUri)
                    viewModel.uiState.value = uiState.copy(previewVisible = false, imageUri = null)
                }
                is CameraPreviewUIAction.OnDismissClick->{
                    viewModel.uiState.value = uiState.copy(previewVisible = false, imageUri = null)
                }
            }
        }
    ) { cameraUIAction ->
        when (cameraUIAction) {
            is CameraUIAction.OnCameraClick -> {
                imageCapture.takePicture(context, lensFacing, onImageCaptured={uri ->
                    viewModel.uiState.value = uiState.copy(imageUri = uri, previewVisible = true)
                },
                    onError={
                        viewModel.onError(it)
                    })
            }
            is CameraUIAction.OnSwitchCameraClick -> {
                lensFacing =
                    if (lensFacing == CameraSelector.LENS_FACING_BACK)
                        CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK
            }
            is CameraUIAction.OnGalleryViewClick -> {
                if (true == context.getOutputDirectory().listFiles()?.isNotEmpty()) {
                    galleryLauncher.launch("image/*")
                }
            }
            is CameraUIAction.OnDismissClick -> {
                onImageCaptured(null)
            }
        }
    }
}


@SuppressLint("RestrictedApi")
@Composable
private fun CameraPreviewView(
    coroutineScope:  CoroutineScope,
    uiState: CameraUiState,
    imageCapture: ImageCapture,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    cameraPreviewUIAction: (CameraPreviewUIAction) -> Unit,
    cameraUIAction: (CameraUIAction) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    val previewView = remember { PreviewView(context) }
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.imageUri != null && uiState.previewVisible){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(0.1f),
                    verticalArrangement = Arrangement.Top
                ) {
                    PreviewControlsTop(cameraPreviewUIAction)
                }
                AsyncImage(
                    model = uiState.imageUri,  // URL de la imagen
                    contentDescription = "Imagen cargada de la web",
                    modifier = Modifier
                        .fillMaxSize().weight(0.8f),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.fillMaxWidth().weight(0.1f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    PreviewControlsBottom(cameraPreviewUIAction)
                }
            }
        }else{
        AndroidView({ previewView },
            modifier = Modifier.fillMaxSize()
                .pointerInput(Unit){
                    detectTapGestures { offset ->
                        coroutineScope.launch {
                            previewView.controller?.cameraControl?.let {
                                triggerFocusAtPoint(
                                    offset.x,
                                    offset.y,
                                    context,
                                    previewView,
                                    it
                                )
                            }
                        }
                    }
                }
        ) {
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Top
        ) {
            CameraControlsTop(cameraUIAction)
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            CameraControlsBottom(cameraUIAction)
        }
        }

    }
}

@Composable
fun PreviewControlsTop(previewUIAction: (CameraPreviewUIAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CameraControl(
            Icons.Outlined.Crop,
            R.string.icn_camera_view_view_crop_content_description,
            modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)),
            onClick = { previewUIAction(CameraPreviewUIAction.OnCropOptions) }
        )
    }
}

@Composable
fun PreviewControlsBottom(previewUIAction: (CameraPreviewUIAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CameraControl(
            Icons.Outlined.Close,
            R.string.icn_camera_view_view_close_content_description,
            modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)),
            onClick = { previewUIAction(CameraPreviewUIAction.OnDismissClick) }
        )

        CameraControl(
            Icons.Outlined.Check,
            R.string.icn_camera_view_view_confirm_content_description,
            modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)),
            onClick = { previewUIAction(CameraPreviewUIAction.OnConfirmClick) }
        )
    }
}

@Composable
fun CameraControlsTop(cameraUIAction: (CameraUIAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CameraControl(
            Icons.Outlined.Close,
            R.string.icn_camera_view_view_gallery_content_description,
            modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)),
            onClick = { cameraUIAction(CameraUIAction.OnDismissClick) }
        )
    }
}

@Composable
fun CameraControlsBottom(cameraUIAction: (CameraUIAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CameraControl(
            Icons.Outlined.PhotoLibrary,
            R.string.icn_camera_view_view_gallery_content_description,
            modifier= Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)),
            onClick = { cameraUIAction(CameraUIAction.OnGalleryViewClick) }
        )

        CameraControl(
            Icons.Sharp.Lens,
            R.string.icn_camera_view_camera_shutter_content_description,
            modifier= Modifier
                .size(64.dp)
                .padding(1.dp)
                .border(1.dp, Color.White, CircleShape),
            onClick = { cameraUIAction(CameraUIAction.OnCameraClick) }
        )

        CameraControl(
            Icons.Outlined.FlipCameraAndroid,
            R.string.icn_camera_view_switch_camera_content_description,
            modifier= Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)),
            onClick = { cameraUIAction(CameraUIAction.OnSwitchCameraClick) }
        )



    }
}


@Composable
fun CameraControl(
    imageVector: ImageVector,
    contentDescId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector,
            contentDescription = stringResource(id = contentDescId),
            modifier = modifier.background(Color.Transparent),
            tint = Color.White
        )
    }

}