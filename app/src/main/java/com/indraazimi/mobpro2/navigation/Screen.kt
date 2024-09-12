package com.indraazimi.mobpro2.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("loginScreen")
    data object Profile : Screen("profileScreen")
    data object AddData : Screen("addDataScreen")
    data object AddClass : Screen("addClassScreen")
    data object StudentList : Screen("studentListScreen/{classId}") {
        fun withClassID(classId: String) = "studentListScreen/$classId"
    }
    data object ModuleList : Screen("moduleListScreen/{classId}") {
        fun withClassID(classId: String) = "moduleListScreen/$classId"
    }
    data object ClassMenu : Screen("classMenuScreen/{classId}") {
        fun withClassID(classId: String) = "classMenuScreen/$classId"
    }
}