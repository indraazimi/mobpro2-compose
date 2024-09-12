package com.indraazimi.mobpro2.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2.ui.screen.AddClassScreen
import com.indraazimi.mobpro2.ui.screen.AddDataScreen
import com.indraazimi.mobpro2.ui.screen.ClassMenuList
import com.indraazimi.mobpro2.ui.screen.LoginScreen
import com.indraazimi.mobpro2.ui.screen.ModuleListScreen
import com.indraazimi.mobpro2.ui.screen.ProfileScreen
import com.indraazimi.mobpro2.ui.screen.StudentListScreen

@SuppressLint("NewApi")
@Composable
fun SetupNavGraph(navController: NavHostController, modifier: Modifier, user: MutableState<FirebaseUser?>) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(
            route = Screen.Login.route,
        ) {
            LoginScreen(navController = navController, user = user, modifier = modifier)
        }
        composable(
            route = Screen.AddData.route,
        ) {
            AddDataScreen(navController, user, modifier)
        }
        composable(
            route = Screen.Profile.route,
        ) {
            ProfileScreen(navController, user, modifier)
        }
        composable(
            route = Screen.AddClass.route,
        ) {
            AddClassScreen(navController, user, modifier)
        }
        composable(
            route = Screen.StudentList.route,
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) {
            StudentListScreen(it.arguments?.getString("classId") ?: "", modifier, user, navController)
        }
        composable(
            route = Screen.ModuleList.route,
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) {
            ModuleListScreen(classId = it.arguments?.getString("classId") ?: "", modifier = modifier, user = user, navController = navController)
        }
        composable(
            route = Screen.ClassMenu.route,
            arguments = listOf(navArgument("classId") { type = NavType.StringType })
        ) {
            ClassMenuList(it.arguments?.getString("classId") ?: "", navController = navController, modifier = modifier, user = user)
        }
    }
}