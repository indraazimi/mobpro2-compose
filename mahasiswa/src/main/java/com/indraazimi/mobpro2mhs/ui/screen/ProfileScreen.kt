package com.indraazimi.mobpro2mhs.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2mhs.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Kelas

@Composable
fun ProfileScreen(user: MutableState<FirebaseUser?>, modifier: Modifier) {
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
        ProfileCard(
            photoUrl = user.value?.photoUrl.toString(),
            name = mahasiswa?.nama ?: "",
            nim = mahasiswa?.nim ?: "",
            email = user.value?.email ?: "",
            kelas = kelas?.nama ?: "",
        )
    }
}

@Composable
fun ProfileCard(
    photoUrl: String?,
    name: String,
    nim: String,
    email: String,
    kelas: String,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(1.dp, Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Gray, CircleShape)
                ) {
                    Text(
                        text = nim,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$name ($nim)",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
            Text(
                text = kelas,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ClassList(kelas: Kelas, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable {
                onClick()
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = kelas.nama,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}