package com.vhenriquez.txwork.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppSettingsAlt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.model.CompanyEntity
import com.vhenriquez.txwork.model.UserEntity
import com.vhenriquez.txwork.navigation.Drawer
import com.vhenriquez.txwork.navigation.Main
import com.vhenriquez.txwork.screens.companies.CompanyItem
import com.vhenriquez.txwork.screens.home.HomeUiState

@Composable
fun AppDrawer(
    user: UserEntity?,
    companyIdSelected: String,
    companies: List<CompanyEntity>,
    route: String,
    modifier: Modifier = Modifier,
    navigateToActivities: (String) -> Unit = {},
    openScreen: (Any) -> Unit = {},
    navigateTo: (Any) -> Unit = {},
    closeDrawer: () -> Unit = {},
    onTitleChange: (String) -> Unit,
    onShowLogoutDialog: (Boolean) -> Unit,
    onAppSettingClick: () -> Unit,
    updateSelectedCompany: (CompanyEntity) -> Unit
) {
    ModalDrawerSheet(modifier = Modifier.width(250.dp).fillMaxHeight()) {
        DrawerHeader(modifier.wrapContentHeight(), user)
        Column(Modifier.weight(1f)) {
            NavigationDrawerItem(
                label = { Text(text = stringResource(id = R.string.drawer_addBussines),
                    style = MaterialTheme.typography.labelMedium)},
                selected = false,
                onClick = {openScreen(Main.EditCompanyApp())},
                icon = { Icon(imageVector = Icons.Outlined.AddCircleOutline, contentDescription = null) },
                shape = MaterialTheme.shapes.small
            )
            LazyColumn {
                items(companies, key = { it.id }) { company ->
                    NavigationDrawerItem(
                        label = { Text(text = company.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)},
                        selected = company.id == companyIdSelected,
                        onClick = {updateSelectedCompany(company) },
                        icon = { AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(company.logo ?: R.drawable.profile)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.profile),
                            contentDescription = stringResource(R.string.app_name),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.card_image_size_business_drawer))
                                .padding(4.dp)
                                .clip(CircleShape),
                        ) },
                        shape = MaterialTheme.shapes.small
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(10.dp))
            Column(Modifier.verticalScroll(rememberScrollState())) {
                AnimatedVisibility(companyIdSelected.isNotEmpty()) {
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = R.string.drawer_activities),
                            style = MaterialTheme.typography.labelLarge)},
                        selected = route.contains(Drawer.Activities.toString()),
                        onClick = {
                            onTitleChange("Actividades")
                            navigateToActivities(companyIdSelected)
                            closeDrawer()
                        },
                        icon = { Icon(imageVector = Icons.Filled.Construction, contentDescription = null) },
                        shape = MaterialTheme.shapes.small
                    )
                }

                if (user != null) {
                    AnimatedVisibility(companyIdSelected.isNotEmpty() && user.roles["instruments"] == true) {
                        NavigationDrawerItem(
                            label = { Text(text = stringResource(id = R.string.drawer_instruments),
                                style = MaterialTheme.typography.labelLarge) },
                            selected = route.contains(Drawer.Instruments.toString()),
                            onClick = {
                                navigateTo(Drawer.Instruments(companyIdSelected))
                                onTitleChange("Instrumentos")
                                closeDrawer()
                            },
                            icon = { Icon(imageVector = Icons.Filled.DeviceThermostat, contentDescription = null) },
                            shape = MaterialTheme.shapes.small
                        )
                    }
                    AnimatedVisibility(companyIdSelected.isNotEmpty() && user.roles["users"] == true) {
                        NavigationDrawerItem(
                            label = { Text(text = stringResource(id = R.string.drawer_users),
                                style = MaterialTheme.typography.labelLarge) },
                            selected = route.contains(Drawer.Users.toString()),
                            onClick = {
                                navigateTo(Drawer.Users(companyIdSelected))
                                onTitleChange("Usuarios")
                                closeDrawer()
                            },
                            icon = { Icon(imageVector = Icons.Filled.Group, contentDescription = null) },
                            shape = MaterialTheme.shapes.small
                        )}
                    AnimatedVisibility(companyIdSelected.isNotEmpty() && user.roles["business"] == true) {
                        NavigationDrawerItem(
                            label = { Text(text = stringResource(id = R.string.drawer_business),
                                style = MaterialTheme.typography.labelLarge) },
                            selected = route.contains(Drawer.Business.toString()),
                            onClick = {
                                navigateTo(Drawer.Business(companyIdSelected))
                                onTitleChange("Empresas")
                                closeDrawer()
                            },
                            icon = { Icon(imageVector = Icons.Filled.Factory, contentDescription = null) },
                            shape = MaterialTheme.shapes.small
                        )}
                    AnimatedVisibility(companyIdSelected.isNotEmpty() && user.roles["patterns"] == true) {
                        Column {
                            HorizontalDivider()
                            Text(
                                text = stringResource(id = R.string.drawerTitlePatterns),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(8.dp,2.dp))
                            NavigationDrawerItem(
                                label = { Text(text = stringResource(id = R.string.drawer_patterns),
                                    style = MaterialTheme.typography.labelLarge) },
                                selected = route.contains(Drawer.Patterns.toString()),
                                onClick = {
                                    navigateTo(Drawer.Patterns(companyIdSelected))
                                    onTitleChange("Patrones")
                                    closeDrawer()
                                },
                                icon = { Icon(imageVector = Icons.Default.Star, contentDescription = null) },
                                shape = MaterialTheme.shapes.small
                            )
                            NavigationDrawerItem(
                                label = { Text(text = stringResource(id = R.string.drawer_certificates),
                                    style = MaterialTheme.typography.labelLarge) },
                                selected = route.contains(Drawer.Certificates.toString()),
                                onClick = {
                                    navigateTo(Drawer.Certificates(companyIdSelected))
                                    onTitleChange("Certificados")
                                    closeDrawer()
                                },
                                icon = { Icon(imageVector = Icons.Filled.Description, contentDescription = null) },
                                shape = MaterialTheme.shapes.small
                            )
                        }
                    }
                    AnimatedVisibility(companyIdSelected.isNotEmpty() && user.roles["tools"] == true) {
                        Column {
                            HorizontalDivider()
                            Text(
                                text = stringResource(id = R.string.drawerTitleTools),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(8.dp,2.dp))
                            NavigationDrawerItem(
                                label = { Text(text = stringResource(id = R.string.drawer_calculatorPV),
                                    style = MaterialTheme.typography.labelLarge) },
                                selected = route.contains(Drawer.CalculatorPV.toString()),
                                onClick = {
                                    navigateTo(Drawer.CalculatorPV)
                                    onTitleChange("Calculadora PV")
                                    closeDrawer()
                                },
                                icon = { Icon(imageVector = Icons.Filled.Calculate, contentDescription = null) },
                                shape = MaterialTheme.shapes.small
                            )
                            NavigationDrawerItem(
                                label = { Text(text = stringResource(id = R.string.drawer_calculatorDP),
                                    style = MaterialTheme.typography.labelLarge) },
                                selected = route.contains(Drawer.CalculatorDP.toString()),
                                onClick = {
                                    navigateTo(Drawer.CalculatorDP)
                                    onTitleChange("Calculadora DP")
                                    closeDrawer()
                                },
                                icon = { Icon(imageVector = Icons.Filled.Calculate, contentDescription = null) },
                                shape = MaterialTheme.shapes.small
                            )
                        }
                    }
                }
            }
        }
        HorizontalDivider(Modifier.padding(10.dp))
        Column(Modifier.wrapContentHeight()) {
            NavigationDrawerItem(
                label = { Text(text = stringResource(R.string.exitApp),
                    style = MaterialTheme.typography.labelMedium)},
                selected = false,
                onClick = {onShowLogoutDialog(true)},
                icon = { Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "exitIcon") },
                shape = MaterialTheme.shapes.small
            )
            NavigationDrawerItem(
                label = { Text(text = stringResource(id = R.string.settings_app),
                    style = MaterialTheme.typography.labelMedium)},
                selected = false,
                onClick = {onAppSettingClick()},
                icon = { Icon(imageVector = Icons.Filled.AppSettingsAlt, contentDescription = null) },
                shape = MaterialTheme.shapes.small
            )
        }

    }
}

@Composable
fun DrawerHeader(modifier: Modifier, user: UserEntity?) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondary)
            .padding(dimensionResource(id = R.dimen.header_padding))
            .fillMaxWidth()
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user?.photoUrl ?: R.drawable.profile)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.profile),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.header_image_size))
                .clip(CircleShape),
        )

        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.spacer_padding)))

        Column {
            Text(
                text = user?.userName ?: "",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            Text(
                text = user?.email ?: "",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Preview
@Composable
fun DrawerHeaderPreview() {
    AppDrawer(
        modifier = Modifier,
        route = Drawer.Users.toString(),
        user = UserEntity(userName = "name", email = "email@email.com", roles = mapOf(
            "instruments" to true
        )),
        onTitleChange = {},
        onShowLogoutDialog = {},
        onAppSettingClick = {},
        companies = listOf(CompanyEntity(name = "Mi empresa1", id = "4"), CompanyEntity(name = "Mi empresa 2")),
        updateSelectedCompany = {},
        companyIdSelected = "4"
    )
}