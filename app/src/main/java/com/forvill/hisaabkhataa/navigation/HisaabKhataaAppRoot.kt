package com.forvill.hisaabkhataa.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.forvill.hisaabkhataa.core.i18n.appStrings
import com.forvill.hisaabkhataa.data.repository.ProductionLedgerRepository
import com.forvill.hisaabkhataa.feature.auth.BusinessSetupScreen
import com.forvill.hisaabkhataa.feature.auth.MobileLoginScreen
import com.forvill.hisaabkhataa.feature.auth.OtpScreen
import com.forvill.hisaabkhataa.feature.home.AddEntryScreen
import com.forvill.hisaabkhataa.feature.home.HomeScreen
import com.forvill.hisaabkhataa.feature.home.PartyDetailScreen
import com.forvill.hisaabkhataa.feature.home.PartyEditorScreen
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
    val context = LocalContext.current
    // AppState keeps this prototype easy to evolve until ViewModels and live APIs are introduced.
    val appState = remember { AppState(repository = ProductionLedgerRepository(context)) }
    val strings = appStrings(appState.appLanguage())
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }
    val showTopBar = currentRoute in setOf(
        AppDestination.PartyDetail.route,
        AppDestination.PartyEditor.route,
        AppDestination.AddEntry.route,
        AppDestination.Reminders.route,
        AppDestination.PrivacyPolicy.route,
    )

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(titleForRoute(currentRoute, strings)) },
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
                SplashScreen(strings = strings, onContinue = { navController.navigate(AppDestination.Language.route) })
            }
            composable(AppDestination.Language.route) {
                LanguageSelectionScreen(
                    strings = strings,
                    selectedLanguage = appState.selectedLanguage,
                    onSelectLanguage = appState::updateSelectedLanguage,
                    onContinue = { navController.navigate(AppDestination.Onboarding.route) },
                )
            }
            composable(AppDestination.Onboarding.route) {
                OnboardingScreen(
                    strings = strings,
                    pages = appState.onboardingPages,
                    onContinue = { navController.navigate(AppDestination.Auth.route) },
                )
            }
            composable(AppDestination.Auth.route) {
                MobileLoginScreen(
                    strings = strings,
                    mobileNumber = appState.mobileNumber,
                    onMobileChange = { appState.mobileNumber = it },
                    onContinue = { navController.navigate(AppDestination.Otp.route) },
                )
            }
            composable(AppDestination.Otp.route) {
                OtpScreen(
                    strings = strings,
                    otpCode = appState.otpCode,
                    onOtpChange = { appState.otpCode = it },
                    onVerify = { navController.navigate(AppDestination.Setup.route) },
                )
            }
            composable(AppDestination.Setup.route) {
                BusinessSetupScreen(
                    strings = strings,
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
                    strings = strings,
                    profile = appState.businessProfile,
                    parties = appState.parties,
                    onAddEntry = { type ->
                        appState.resetEntryDraft(type)
                        navController.navigate(AppDestination.AddEntry.route)
                    },
                    onOpenParty = { partyId ->
                        appState.selectParty(partyId)
                        navController.navigate(AppDestination.PartyDetail.route)
                    },
                    onOpenReminders = { navController.navigate(AppDestination.Reminders.route) },
                )
            }
            composable(AppDestination.Parties.route) {
                PartyListScreen(
                    strings = strings,
                    parties = appState.parties,
                    onSelectParty = { partyId ->
                        appState.selectParty(partyId)
                        navController.navigate(AppDestination.PartyDetail.route)
                    },
                    onAddEntry = { type ->
                        appState.resetEntryDraft(type)
                        navController.navigate(AppDestination.AddEntry.route)
                    },
                    onAddParty = {
                        appState.startCreateParty()
                        navController.navigate(AppDestination.PartyEditor.route)
                    },
                )
            }
            composable(AppDestination.PartyDetail.route) {
                PartyDetailScreen(
                    strings = strings,
                    party = appState.selectedParty(),
                    onAddEntry = { type ->
                        appState.resetEntryDraft(type)
                        navController.navigate(AppDestination.AddEntry.route)
                    },
                    onEditParty = {
                        appState.startEditSelectedParty()
                        navController.navigate(AppDestination.PartyEditor.route)
                    },
                )
            }
            composable(AppDestination.PartyEditor.route) {
                PartyEditorScreen(
                    strings = strings,
                    draft = appState.partyDraft,
                    isEditMode = appState.editingPartyId != null,
                    onNameChange = appState::updatePartyName,
                    onPhoneChange = appState::updatePartyPhone,
                    onCreditLimitChange = appState::updatePartyCreditLimit,
                    onTagsChange = appState::updatePartyTags,
                    onSave = {
                        appState.savePartyDraft()
                        navController.popBackStack()
                    },
                )
            }
            composable(AppDestination.AddEntry.route) {
                AddEntryScreen(
                    strings = strings,
                    party = appState.selectedParty(),
                    draft = appState.addEntryDraft,
                    apiPayload = appState.latestLedgerPayload,
                    onAmountChange = appState::updateEntryAmount,
                    onNoteChange = appState::updateEntryNote,
                    onDueDateChange = appState::updateEntryDueDateLabel,
                    onDueDateValueChange = appState::updateEntryDueDateValue,
                    onTypeChange = appState::setEntryType,
                    onSave = {
                        appState.saveEntry()
                        navController.popBackStack()
                    },
                )
            }
            composable(AppDestination.Reminders.route) {
                ReminderScreen(strings = strings, reminders = appState.reminders)
            }
            composable(AppDestination.Reports.route) {
                ReportsScreen(
                    strings = strings,
                    reports = appState.reports,
                    filter = appState.reportFilter,
                    lastExport = appState.lastReportExport,
                    onRangeKeyChange = appState::updateReportRange,
                    onStartDateChange = appState::updateReportStartDate,
                    onEndDateChange = appState::updateReportEndDate,
                    onExport = appState::exportReports,
                )
            }
            composable(AppDestination.Settings.route) {
                SettingsScreen(
                    strings = strings,
                    profile = appState.businessProfile,
                    settings = appState.settings,
                    syncQueue = appState.syncQueue,
                    onProfileChange = appState::updateProfile,
                    onLanguageChange = appState::updateSelectedLanguage,
                    onReminderToggle = appState::updateReminderPreference,
                    onRetrySync = appState::retrySyncQueue,
                    onOpenPrivacyPolicy = { navController.navigate(AppDestination.PrivacyPolicy.route) },
                )
            }
            composable(AppDestination.PrivacyPolicy.route) {
                PrivacyPolicyScreen(strings = strings)
            }
        }
    }
}

private fun titleForRoute(route: String?, strings: com.forvill.hisaabkhataa.core.i18n.AppStrings): String {
    return when (route) {
        AppDestination.PartyDetail.route -> strings.partyDetailTitle
        AppDestination.PartyEditor.route -> strings.addParty
        AppDestination.AddEntry.route -> strings.addEntryTopTitle
        AppDestination.Reminders.route -> strings.remindersTitle
        AppDestination.PrivacyPolicy.route -> strings.privacyPolicyTitle
        else -> ""
    }
}
