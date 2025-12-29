package com.mbdrmod.delivery.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FormEntry : Screen("form_entry")
}
