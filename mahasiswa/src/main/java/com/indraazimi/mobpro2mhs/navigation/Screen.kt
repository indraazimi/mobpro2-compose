package com.indraazimi.mobpro2mhs.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("loginScreen")
    data object Profile : Screen("profileScreen")
    data object AddData : Screen("addDataScreen")
}