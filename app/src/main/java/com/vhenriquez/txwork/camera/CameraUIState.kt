package com.vhenriquez.txwork.camera

import android.graphics.Bitmap
import android.net.Uri
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity

data class CameraUiState(
    val previewVisible: Boolean = false,
    val imageUri: Uri? = null
)