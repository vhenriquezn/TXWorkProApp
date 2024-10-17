package com.vhenriquez.txwork.navigation

import androidx.compose.ui.window.Dialog
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.vhenriquez.txwork.screens.activities.ActivitiesScreen
import com.vhenriquez.txwork.screens.certificates.CertificatesScreen
import com.vhenriquez.txwork.screens.companies.CompaniesScreen
import com.vhenriquez.txwork.screens.edit_certificate.EditCertificateScreen
import com.vhenriquez.txwork.screens.edit_company_app.EditCompanyAppScreen
import com.vhenriquez.txwork.screens.instruments.InstrumentsScreen
import com.vhenriquez.txwork.screens.patterns.PatternsScreen
import com.vhenriquez.txwork.screens.users.UsersScreen
import com.vhenriquez.txwork.utils.TXWorkAppState

fun NavGraphBuilder.drawerNavGraph(
    appState: TXWorkAppState, startDestination: Any) {
    navigation<Drawer>(
        startDestination = startDestination
    ){
        composable<Drawer.Activities> {
            ActivitiesScreen(
                openScreen = {route -> appState.navigate(route)})
        }

        composable<Drawer.Instruments> {
            InstrumentsScreen(
                openScreen = {route -> appState.navigate(route)}
            )
        }
        composable<Drawer.Users> {
            UsersScreen(
                openScreen = {route -> appState.navigate(route)}
            )
        }
        composable<Drawer.Business> {
            CompaniesScreen(
                openScreen = {route -> appState.navigate(route)}
            )
        }
        composable<Drawer.Patterns> {
            PatternsScreen(
                openScreen = {route -> appState.navigate(route)}
            )
        }
        composable<Drawer.Certificates> {
            CertificatesScreen(
                openScreen = {route -> appState.navigate(route)}
            )
        }
        composable<Drawer.CalculatorPV> {
            //InstrumentsScreen()
        }
        composable<Drawer.CalculatorDP> {
            //InstrumentsScreen()
        }

//        dialog<Drawer.EditCompanyApp>{
//            EditCompanyAppScreen(
//                popUp = {appState.popUp()}
//            )
//        }

    }
}