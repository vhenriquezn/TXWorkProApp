package com.vhenriquez.txwork.screens.login

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.ext.isValidEmail
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.AuthRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferencesRepository
) : TXWorkViewModel() {

    var uiState = mutableStateOf(LoginUiState())
        private set
    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(navigateToMain: () -> Unit) {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }

        if (password.isBlank()) {
            SnackbarManager.showMessage(R.string.empty_password_error)
            return
        }

        launchCatching {
            authRepository.login(email,password).collectLatest {result->
                when (result) {
                    is Resource.Success -> {
                        result.data?.user?.let {
                            userPreferences.updateUser(it)
                        }
                        navigateToMain()
                    }
                    is Resource.Error -> {
                        SnackbarManager.showMessage(R.string.login_error)
                        uiState.value = uiState.value.copy(isLoading = false)
                    }
                    is Resource.Loading ->
                        uiState.value = uiState.value.copy(isLoading = true)
                }
            }
        }
    }
}