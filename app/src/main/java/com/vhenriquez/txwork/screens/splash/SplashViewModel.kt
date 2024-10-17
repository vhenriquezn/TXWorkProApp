package com.vhenriquez.txwork.screens.splash

import com.vhenriquez.txwork.TXWorkViewModel
import com.vhenriquez.txwork.model.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val userPreferencesRepository: UserPreferencesRepository
) : TXWorkViewModel() {

    fun onAppStart(navigateToMain: () -> Unit, navigateToLogin: () -> Unit) {
        launchCatching {
            userPreferencesRepository.isUserLoggedInFlow.collect{
                if (it) {
                    navigateToMain()
                } else {
                    navigateToLogin()
                }
            }
        }

    }
}