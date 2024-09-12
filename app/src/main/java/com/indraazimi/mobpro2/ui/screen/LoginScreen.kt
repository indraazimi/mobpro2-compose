package com.indraazimi.mobpro2.ui.screen

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2.R
import com.indraazimi.mobpro2.navigation.Screen
import com.indraazimi.mobpro2.ui.theme.Mobpro2Theme
import com.indraazimi.mobpro2.viewmodels.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier, user: MutableState<FirebaseUser?>, navController: NavController) {
    val dataViewModel: DataViewModel = viewModel()

    val dosen by dataViewModel.selectedDosen.collectAsStateWithLifecycle()
    val loading by dataViewModel.loading.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = user.value?.uid) {
        user.value?.uid?.let { uid ->
            dataViewModel.getDosenByID(uid)
        }
    }

    LaunchedEffect(key1 = loading) {
        if (loading == false) {
            if (dosen == null && user.value != null) {
                navController.navigate(Screen.AddData.route)
            }

            if (dosen != null && user.value != null) {
                navController.navigate(Screen.Profile.route)
            }
        }
    }

    val launcher =
        rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
            if (result.resultCode == RESULT_OK) {
                user.value = FirebaseAuth.getInstance().currentUser
            }
        }

    var showMenu by remember { mutableStateOf(false) }

    Mobpro2Theme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.profile))
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
                verticalArrangement = Arrangement.Center
            ) {
                if (user.value != null) {
                    Text(text = user.value?.displayName ?: "")
                    Spacer(modifier = Modifier.height(16.dp))
                }
                AsyncImage(
                    model = FirebaseAuth.getInstance().currentUser?.photoUrl,
                    contentDescription = stringResource(id = R.string.profile_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (user.value == null) {
                    Button(
                        onClick = {
                            val providers = arrayListOf(
                                AuthUI.IdpConfig.GoogleBuilder().build()
                            )

                            val signInIntent = AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build()

                            launcher.launch(signInIntent)
                        },
                    ) {
                        Text(text = stringResource(R.string.login))
                    }
                }
            }
        }
    }
}