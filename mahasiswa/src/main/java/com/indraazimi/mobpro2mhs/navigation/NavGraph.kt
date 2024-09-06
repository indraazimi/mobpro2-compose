package com.indraazimi.mobpro2mhs.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2mhs.ui.screen.AddDataScreen
import com.indraazimi.mobpro2mhs.ui.screen.LoginScreen
import com.indraazimi.mobpro2mhs.ui.screen.MapScreen
import com.indraazimi.mobpro2mhs.ui.screen.ProfileScreen

@SuppressLint("NewApi")
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    modifier: Modifier,
    user: MutableState<FirebaseUser?>,
    lat: MutableState<Double?>,
    lon: MutableState<Double?>,
    address: MutableState<String>
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(
            route = Screen.Login.route,
        ) {
            LoginScreen(navController = navController, user = user, modifier = modifier)
        }
        composable(
            route = Screen.Profile.route,
        ) {
            ProfileScreen(user = user, modifier = modifier)
        }
        composable(
            route = Screen.AddData.route,
        ) {
            AddDataScreen(navController = navController, user = user, modifier = modifier, lat = lat, lon = lon, address = address)
        }
        composable(
            route = Screen.Map.route,
        ) {
            MapScreen(navController = navController, lat = lat, lon = lon, address = address, modifier = modifier)
        }
    }
}