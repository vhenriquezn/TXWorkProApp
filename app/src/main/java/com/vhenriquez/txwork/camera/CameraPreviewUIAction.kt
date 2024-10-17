package com.vhenriquez.txwork.camera

sealed class CameraPreviewUIAction {
    data object OnCropOptions : CameraPreviewUIAction()
    data object OnConfirmClick : CameraPreviewUIAction()
    data object OnDismissClick : CameraPreviewUIAction()
}