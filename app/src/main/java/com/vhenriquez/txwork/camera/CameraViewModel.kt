package com.vhenriquez.txwork.camera


import androidx.camera.core.ImageCaptureException
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.common.snackbar.SnackbarMessage
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.ActivityEntity
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.PatternEntity
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.model.repository.AuthRepository
import com.vhenriquez.txwork.navigation.Main
import com.vhenriquez.txwork.screens.login.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(

) : TXWorkViewModel() {
    fun onError(imageCaptureError: ImageCaptureException) {
        launchCatching {
            SnackbarManager.showMessage(SnackbarMessage.StringSnackbar(imageCaptureError.message?:""))
        }
    }


    var uiState = mutableStateOf(CameraUiState())
        private set
}