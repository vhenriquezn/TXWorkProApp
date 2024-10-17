package com.vhenriquez.txwork.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.vhenriquez.txwork.screens.home.HomeScreen
import com.vhenriquez.txwork.screens.activityDetail.ActivityDetailScreen
import com.vhenriquez.txwork.screens.activityDetail.AddInstrumentsToActivityScreen
import com.vhenriquez.txwork.screens.activityDetail.AddUsersToActivityScreen
import com.vhenriquez.txwork.screens.activityDetail.MyInstrumentDetail
import com.vhenriquez.txwork.screens.edit_activity.EditActivityScreen
import com.vhenriquez.txwork.screens.edit_certificate.EditCertificateScreen
import com.vhenriquez.txwork.screens.edit_company.EditCompanyScreen
import com.vhenriquez.txwork.screens.edit_company_app.EditCompanyAppScreen
import com.vhenriquez.txwork.screens.edit_instrument.EditInstrumentScreen
import com.vhenriquez.txwork.screens.edit_pattern.EditPatternScreen
import com.vhenriquez.txwork.screens.edit_user.EditUserScreen
import com.vhenriquez.txwork.utils.TXWorkAppState

fun NavGraphBuilder.mainNavGraph(appState: TXWorkAppState) {
    navigation<Main>(
        startDestination = Main.Home
    ){
        composable<Main.Home>{
            HomeScreen(
                appState = appState,
                restartApp = {route -> appState.clearAndNavigate(route)},
         )
        }

        composable<Main.ActivityDetail>{
            ActivityDetailScreen(
                onNavigateBack = {appState.popUp()},
                openScreen = {route -> appState.navigate(route)})
        }

        dialog<Main.EditActivity>{
            EditActivityScreen(
                popUp = {appState.popUp()}
            )
        }

        dialog<Main.AddInstrumentsToActivity>{
            AddInstrumentsToActivityScreen(
                openScreen = {route -> appState.navigate(route)},
                popUp = {appState.popUp()}
            )
        }

        dialog<Main.AddUsersToActivity>{
            AddUsersToActivityScreen(
                openScreen = {route -> appState.navigate(route)},
                popUp = {appState.popUp()}
            )
        }

        dialog<Main.EditInstrument>{
            EditInstrumentScreen(
                popUp = {appState.popUp()}
            )
        }

        dialog<Main.EditPattern>{
            EditPatternScreen(
                popUp = {appState.popUp()}
            )
        }

        dialog<Main.EditUser>{
            EditUserScreen(
                popUp = {appState.popUp()}
            )
        }
        dialog<Main.EditCompany>{
            EditCompanyScreen(
                popUp = {appState.popUp()}
            )
        }
        dialog<Main.EditCertificate>{
            EditCertificateScreen(
                popUp = {appState.popUp()}
            )
        }
        dialog<Main.EditCompanyApp>{
            EditCompanyAppScreen(
                popUp = {appState.popUp()}
            )
        }

        composable<Main.InstrumentDetail> {
            //MyInstrumentDetail() { }
//            MyInstrumentDetail(
//                generateReport = {}
//            ) {
//
//            }
        }
    }
}