package com.indraazimi.mobpro2mhs.ui.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2mhs.R
import com.indraazimi.mobpro2mhs.navigation.Screen
import com.indraazimi.mobpro2mhs.viewmodels.DataViewModel

@Composable
fun LoginScreen(modifier: Modifier = Modifier, user: MutableState<FirebaseUser?>, navController: NavController) {
    val dataViewModel: DataViewModel = viewModel()

    val mahasiswa by dataViewModel.selectedMahasiswa.collectAsStateWithLifecycle()

    val loading by dataViewModel.loading.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = user.value?.uid) {
        user.value?.uid?.let { uid ->
            dataViewModel.getMahasiswaByID(uid)
        }
    }

    LaunchedEffect (key1 = loading) {
        if (loading == false) {
            if (mahasiswa == null && user.value != null) {
                navController.navigate(Screen.AddData.route)
            }

            if (mahasiswa != null && user.value != null) {
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

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (user.value != null) {
            Text(text = user.value?.displayName?: "")
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