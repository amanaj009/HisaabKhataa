package com.forvill.hisaabkhataa.navigation

sealed class AppDestination(val route: String) {
    data object Splash : AppDestination("splash")
    data object Language : AppDestination("language")
    data object Onboarding : AppDestination("onboarding")
    data object Auth : AppDestination("auth")
    data object Otp : AppDestination("otp")
    data object Setup : AppDestination("setup")
    data object Home : AppDestination("home")
    data object Parties : AppDestination("parties")
    data object PartyDetail : AppDestination("party_detail")
    data object AddEntry : AppDestination("add_entry")
    data object Reminders : AppDestination("reminders")
    data object Reports : AppDestination("reports")
    data object Settings : AppDestination("settings")
    data object PrivacyPolicy : AppDestination("privacy_policy")
}
