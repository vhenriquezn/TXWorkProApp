package com.vhenriquez.txwork.camera

sealed class CameraUIAction {
    data object OnCameraClick : CameraUIAction()
    data object OnGalleryViewClick : CameraUIAction()
    data object OnSwitchCameraClick : CameraUIAction()
    data object OnDismissClick : CameraUIAction()
}