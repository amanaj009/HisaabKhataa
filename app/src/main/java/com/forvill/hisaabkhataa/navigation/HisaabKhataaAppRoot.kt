package com.forvill.hisaabkhataa.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.forvill.hisaabkhataa.core.data.SampleData
import com.forvill.hisaabkhataa.feature.auth.BusinessSetupScreen
import com.forvill.hisaabkhataa.feature.auth.MobileLoginScreen
import com.forvill.hisaabkhataa.feature.auth.OtpScreen
import com.forvill.hisaabkhataa.feature.home.AddEntryScreen
import com.forvill.hisaabkhataa.feature.home.HomeScreen
import com.forvill.hisaabkhataa.feature.home.PartyDetailScreen
import com.forvill.hisaabkhataa.feature.home.PartyListScreen
import com.forvill.hisaabkhataa.feature.home.ReminderScreen
import com.forvill.hisaabkhataa.feature.language.LanguageSelectionScreen
import com.forvill.hisaabkhataa.feature.onboarding.OnboardingScreen
import com.forvill.hisaabkhataa.feature.reports.ReportsScreen
import com.forvill.hisaabkhataa.feature.settings.PrivacyPolicyScreen
import com.forvill.hisaabkhataa.feature.settings.SettingsScreen
import com.forvill.hisaabkhataa.feature.splash.SplashScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HisaabKhataaAppRoot() {
    val navController = rememberNavController()
    // AppState keeps this prototype easy to evolve until we introduce ViewModels and backend data.
    val appState = remember { AppState() }
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }
    val showTopBar = currentRoute in setOf(
        AppDestination.PartyDetail.route,
        AppDestination.AddEntry.route,
        AppDestination.Reminders.route,
        AppDestination.PrivacyPolicy.route,
    )

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(titleForRoute(currentRoute)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(),
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val destination = backStackEntry?.destination
                    bottomNavItems.forEach { item ->
                        val selected = destination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Splash.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            // Startup flow stays separate from post-login screens so onboarding can evolve independently.
            composable(AppDestination.Splash.route) {
                SplashScreen(onContinue = { navController.navigate(AppDestination.Language.route) })
            }
            composable(AppDestination.Language.route) {
                LanguageSelectionScreen(
                    selectedLanguage = appState.selectedLanguage,
                    onSelectLanguage = { appState.selectedLanguage = it },
                    onContinue = { navController.navigate(AppDestination.Onboarding.route) },
                )
            }
            composable(AppDestination.Onboarding.route) {
                OnboardingScreen(
                    pages = SampleData.onboardingPages,
                    onContinue = { navController.navigate(AppDestination.Auth.route) },
                )
            }
            composable(AppDestination.Auth.route) {
                MobileLoginScreen(
                    mobileNumber = appState.mobileNumber,
                    onMobileChange = { appState.mobileNumber = it },
                    onContinue = { navController.navigate(AppDestination.Otp.route) },
                )
            }
            composable(AppDestination.Otp.route) {
                OtpScreen(
                    otpCode = appState.otpCode,
                    onOtpChange = { appState.otpCode = it },
                    onVerify = { navController.navigate(AppDestination.Setup.route) },
                )
            }
            composable(AppDestination.Setup.route) {
                BusinessSetupScreen(
                    profile = appState.businessProfile,
                    onProfileChange = appState::updateProfile,
                    onContinue = {
                        navController.navigate(AppDestination.Home.route) {
                            popUpTo(AppDestination.Splash.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(AppDestination.Home.route) {
                HomeScreen(
                    profile = appState.businessProfile,
                    parties = appState.parties,
                    onAddEntry = { navController.navigate(AppDestination.AddEntry.route) },
                    onOpenParty = { partyId ->
                        appState.selectParty(partyId)
                        navController.navigate(AppDestination.PartyDetail.route)
                    },
                    onOpenReminders = { navController.navigate(AppDestination.Reminders.route) },
                )
            }
            composable(AppDestination.Parties.route) {
                PartyListScreen(
                    parties = appState.parties,
                    onSelectParty = { partyId ->
                        appState.selectParty(partyId)
                        navController.navigate(AppDestination.PartyDetail.route)
                    },
                    onAddEntry = { navController.navigate(AppDestination.AddEntry.route) },
                )
            }
            composable(AppDestination.PartyDetail.route) {
                PartyDetailScreen(
                    party = appState.selectedParty(),
                    onAddEntry = { navController.navigate(AppDestination.AddEntry.route) },
                )
            }
            composable(AppDestination.AddEntry.route) {
                AddEntryScreen(
                    party = appState.selectedParty(),
                    onDone = { navController.popBackStack() },
                )
            }
            composable(AppDestination.Reminders.route) {
                ReminderScreen()
            }
            composable(AppDestination.Reports.route) {
                ReportsScreen()
            }
            composable(AppDestination.Settings.route) {
                SettingsScreen(
                    profile = appState.businessProfile,
                    onOpenPrivacyPolicy = { navController.navigate(AppDestination.PrivacyPolicy.route) },
                )
            }
            composable(AppDestination.PrivacyPolicy.route) {
                PrivacyPolicyScreen()
            }
        }
    }
}

private fun titleForRoute(route: String?): String {
    return when (route) {
        AppDestination.PartyDetail.route -> "Party details"
        AppDestination.AddEntry.route -> "Add entry"
        AppDestination.Reminders.route -> "Reminders"
        AppDestination.PrivacyPolicy.route -> "Privacy policy"
        else -> ""
    }
}
