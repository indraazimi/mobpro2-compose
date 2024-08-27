package com.indraazimi.mobpro2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.indraazimi.mobpro2.navigation.Screen
import com.indraazimi.mobpro2.navigation.SetupNavGraph
import com.indraazimi.mobpro2.ui.theme.Mobpro2Theme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val user = remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
            var showMenu by remember { mutableStateOf(false) }

            Mobpro2Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        val currentBackStackEntry = navController.currentBackStackEntryAsState().value

                        val forbiddenRoutes = listOf(
                            Screen.Login.route,
                            Screen.AddData.route,
                        )

                        if (!forbiddenRoutes.contains(currentBackStackEntry?.destination?.route)) {
                            FloatingActionButton(onClick = {
                                navController.navigate(Screen.AddClass.route)
                            }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            }
                        }
                    },
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = stringResource(R.string.app_name))
                            },
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            actions = {
                                val currentBackStackEntry = navController.currentBackStackEntryAsState().value
                                if (currentBackStackEntry?.destination?.route != Screen.Login.route) {
                                    IconButton(onClick = {
                                        showMenu = true
                                    }) {
                                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                                    }

                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            onClick = {
                                                FirebaseAuth.getInstance().signOut()
                                                user.value = null
                                                navController.navigate(Screen.Login.route)
                                            },
                                            text = {
                                                Text(text = stringResource(R.string.logout))
                                            }
                                        )
                                    }
                                }
                            },
                        )
                    },
                ) { paddingValues ->
                    SetupNavGraph(navController = navController, user = user, modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }
}