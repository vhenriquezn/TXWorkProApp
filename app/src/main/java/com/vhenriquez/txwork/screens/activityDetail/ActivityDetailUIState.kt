package com.vhenriquez.txwork.screens.activityDetail

data class ActivityDetailUiState(
    val showDialogObservations: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val currentSelectedIndex: Int = -1,
    val showAttachMenu : Boolean = false,
    val showOptionsMenu: Boolean = false,
    val takePicture: Boolean = false
)