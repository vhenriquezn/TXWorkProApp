package com.vhenriquez.txwork.screens.forgotPassword

import androidx.compose.runtime.mutableStateOf
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.common.ext.isValidEmail
import com.vhenriquez.txwork.common.snackbar.SnackbarManager
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val authRepository: AuthRepository
) : TXWorkViewModel() {

    var email = mutableStateOf("")
        private set

    fun onEmailChange(newValue: String) {
        email.value = newValue
    }

    fun onSendPasswordResetEmailClick(onNavigateBack: () -> Unit) {
        if (!email.value.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }

        launchCatching {
            authRepository.resetPassword(email.value)
            onNavigateBack()
        }
    }
}