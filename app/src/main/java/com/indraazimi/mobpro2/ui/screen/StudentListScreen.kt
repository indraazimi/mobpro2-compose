package com.indraazimi.mobpro2.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.indraazimi.mobpro2.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Mahasiswa

@Composable
fun StudentListScreen(classId: String, modifier: Modifier) {
    val viewModel: DataViewModel = viewModel()
    val mahasiswa by viewModel.allMahasiswa.collectAsStateWithLifecycle()
    val kelas by viewModel.selectedKelas.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = classId) {
        viewModel.getKelasByID(classId)
    }

    LaunchedEffect(key1 = kelas) {
        kelas?.id?.let { id ->
            viewModel.getMahasiswaByKelasID(id)
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = kelas?.nama ?: "",
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
            ) {
                items(mahasiswa) {
                    ItemList(data = it)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ItemList(data: Mahasiswa) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
        Text(text = data.nama, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
        Text(text = data.nim, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
