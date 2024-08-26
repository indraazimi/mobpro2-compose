package com.indraazimi.mobpro2.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.indraazimi.mobpro2.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Mahasiswa

@Composable
fun StudentListScreen(navGraph: NavController, classId: String) {
    val viewModel: DataViewModel = viewModel()
    val mahasiswa by viewModel.allMahasiswa.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.getMahasiswaByKelasID(classId)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(mahasiswa) {
            ItemList(data = it) {
                navGraph.navigate("detailStudentScreen/${it.id}")
            }
        }
    }

}

@Composable
fun ItemList(data: Mahasiswa, onClick: () -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(8.dp),

        ) {
        Text(text = data.id, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
        Text(text = data.nama, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(text = data.nim, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
