package com.indraazimi.mobpro2mhs.navigation

import androidx.compose.runtime.MutableState

sealed class Screen(val route: String) {
    data object Login : Screen("loginScreen")
    data object Profile : Screen("profileScreen")
    data object AddData : Screen("addDataScreen")
    data object Map : Screen("mapScreen") {
        fun createRoute(lat: MutableState<Double>, long: MutableState<Double>, address: MutableState<String>) = "mapScreen/$lat/$long/$address"
    }
}