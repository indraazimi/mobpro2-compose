package com.indraazimi.mobpro2.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("loginScreen")
    data object Profile : Screen("profileScreen")
    data object AddData : Screen("addDataScreen")
    data object AddClass : Screen("addClassScreen")
    data object StudentList : Screen("studentListScreen/{classId}") {
        fun withClassID(classId: String) = "studentListScreen/$classId"
    }
}