package com.indraazimi.mobpro2mhs.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2mhs.R
import com.indraazimi.mobpro2mhs.navigation.Screen
import com.indraazimi.mobpro2mhs.viewmodels.DataViewModel

@Composable
fun ProfileScreen(navController: NavController, user: MutableState<FirebaseUser?>, modifier: Modifier) {
    val viewModel: DataViewModel = viewModel()
    val mahasiswa by viewModel.selectedMahasiswa.collectAsStateWithLifecycle()
    val kelas by viewModel.selectedKelas.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = user.value?.uid) {
        user.value?.uid?.let { uid ->
            viewModel.getMahasiswaByID(uid)
            viewModel.getKelasByMahasiswaID(uid)
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = stringResource(id = R.string.profile),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AsyncImage(
            model = FirebaseAuth.getInstance().currentUser?.photoUrl,
            contentDescription = stringResource(id = R.string.profile_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileDetailRow(label = stringResource(id = R.string.name), value = mahasiswa?.nama ?: "")
        ProfileDetailRow(label = stringResource(id = R.string.email), value = user.value?.email ?: "")
        ProfileDetailRow(label = stringResource(id = R.string.student_id), value = mahasiswa?.nim ?: "")
        ProfileDetailRow(label = stringResource(id = R.string.class_name), value = kelas?.nama ?: "")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            user.value = null
            navController.navigate(Screen.Login.route)
        }) {
            Text(stringResource(id = R.string.logout))
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(2f)
        )
    }
}