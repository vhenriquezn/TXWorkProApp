package com.vhenriquez.txwork.screens.sign_up

import androidx.compose.runtime.mutableStateOf
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.ext.isValidEmail
import com.vhenriquez.txwork.common.ext.isValidPassword
import com.vhenriquez.txwork.common.ext.passwordMatches
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.AuthRepository
import com.vhenriquez.txwork.model.repository.Resource
import com.vhenriquez.txwork.model.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferencesRepository
) : TXWorkViewModel() {
    var uiState = mutableStateOf(SignUpUiState())
    private set

    private val name
        get() = uiState.value.name
    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(name = newValue)
    }
    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun onSignUpClick(navigateTo: () -> Unit) {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(R.string.password_error)
            return
        }

        if (!password.passwordMatches(uiState.value.repeatPassword)) {
            SnackbarManager.showMessage(R.string.password_match_error)
            return
        }

        launchCatching {
            authRepository.signup(name, email, password).collectLatest {result->
                when (result) {
                    is Resource.Success -> {
                        result.data?.user?.let {
                            userPreferences.updateUser(it)
                            navigateTo()
                        }

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