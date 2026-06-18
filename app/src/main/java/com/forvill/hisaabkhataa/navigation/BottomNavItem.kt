package com.forvill.hisaabkhataa.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(AppDestination.Home.route, "Home", Icons.Outlined.Home),
    BottomNavItem(AppDestination.Parties.route, "Parties", Icons.Outlined.Groups),
    BottomNavItem(AppDestination.Reports.route, "Reports", Icons.Outlined.Assessment),
    BottomNavItem(AppDestination.Settings.route, "Settings", Icons.Outlined.Settings),
)
