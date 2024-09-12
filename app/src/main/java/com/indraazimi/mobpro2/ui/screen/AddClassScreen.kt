package com.indraazimi.mobpro2.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2.R
import com.indraazimi.mobpro2.navigation.Screen
import com.indraazimi.mobpro2.ui.theme.Mobpro2Theme
import com.indraazimi.mobpro2.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Kelas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassScreen(navController: NavController, user: MutableState<FirebaseUser?>, modifier: Modifier) {
    val viewModel: DataViewModel = viewModel()

    var nameData by remember { mutableStateOf("") }

    if (user.value == null) {
        navController.navigate(Screen.Login.route)
    }
    var showMenu by remember { mutableStateOf(false) }

    Mobpro2Theme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.add_class))
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    actions = {
                        val currentBackStackEntry =
                            navController.currentBackStackEntryAsState().value
                        if (currentBackStackEntry?.destination?.route != Screen.Login.route) {
                            IconButton(onClick = {
                                showMenu = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = null
                                )
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
                                        showMenu = false
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
            modifier = Modifier.fillMaxSize(),
        ) { paddingValue ->
            Column(
                modifier = modifier
                    .padding(paddingValue)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(text = stringResource(id = R.string.add_class))

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nameData,
                    onValueChange = {
                        if (it.length <= 25) {
                            nameData = it
                        }
                    },
                    label = { Text(stringResource(id = R.string.name)) },
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val newKelas = Kelas(
                            nama = nameData,
                        )

                        viewModel.addKelas(
                            user.value?.uid ?: "",
                            newKelas
                        )

                        navController.popBackStack()
                    }
                ) {
                    Text(stringResource(id = R.string.save))
                }
            }
        }
    }
}