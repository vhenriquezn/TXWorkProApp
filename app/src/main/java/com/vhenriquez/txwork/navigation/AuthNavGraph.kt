package com.vhenriquez.txwork.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vhenriquez.txwork.screens.forgotPassword.ForgotPasswordScreen
import com.vhenriquez.txwork.screens.login.LoginScreen
import com.vhenriquez.txwork.screens.sign_up.SignUpScreen
import com.vhenriquez.txwork.screens.splash.SplashScreen
import com.vhenriquez.txwork.utils.TXWorkAppState


fun NavGraphBuilder.authNavGraph(appState: TXWorkAppState) {

    navigation<Auth>(
        startDestination = Auth.Splash,
    ){
        composable<Auth.Splash>{
            SplashScreen(
                navigateToLogin = {
                    appState.navigate(Auth.Login)
                },
                navigateToMain = {
                    appState.navigateAndPopUp(Main, Auth)
                })
        }

        composable<Auth.Login> {
            LoginScreen(
                navigateToMain = {
                    appState.navigateAndPopUp(Main, Auth)
                },
                navigateToSignUp = {
                    appState.navigate(Auth.SignUp)
                },
                navigateToForgotPassword = {
                    appState.navigate(Auth.ForgotPassword)
                },
                onNavigateBack = {
                    //Apllication.instance.finish()
                })
        }

        composable<Auth.SignUp> {
            SignUpScreen(onNavigateBack = {
                appState.popUp()
            })
        }

        composable<Auth.ForgotPassword> {
            ForgotPasswordScreen(onNavigateBack = {
                appState.popUp()
            })
        }

    }
}