package com.vhenriquez.txwork.screens
import androidx.navigation.NavHostController
import com.vhenriquez.txwork.navigation.Drawer

class AppNavigationActions(private val navController: NavHostController) {

    fun navigateAndPopUp(route: Any, popUp: Any) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }



    fun navigateToActivities() {
        navController.navigate(Drawer.Activities) {
            popUpTo(Drawer.Activities)
        }
    }

    fun navigate(route: Any) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true}
    }

//    fun navigateToInstruments() {
//        navController.navigate(Drawer.Instruments) {
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//    fun navigateToUsers() {
//        navController.navigate(Drawer.Users) {
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//    fun navigateToBusiness() {
//        navController.navigate(Drawer.Business) {
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//    fun navigateToPatterns() {
//        navController.navigate(Drawer.Patterns) {
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//    fun navigateToCertificates() {
//        navController.navigate(Drawer.Certificates) {
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//    fun navigateToCalculatorPV() {
//        navController.navigate(Drawer.CalculatorPV) {
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//    fun navigateToCalculatorDP() {
//        navController.navigate(Drawer.CalculatorDP) {
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
}