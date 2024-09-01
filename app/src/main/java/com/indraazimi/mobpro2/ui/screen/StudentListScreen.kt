package com.indraazimi.mobpro2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.indraazimi.mobpro2.R
import com.indraazimi.mobpro2.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Mahasiswa

@Composable
fun StudentListScreen(classId: String, modifier: Modifier) {
    val viewModel: DataViewModel = viewModel()
    val mahasiswa by viewModel.allMahasiswa.collectAsStateWithLifecycle()
    val kelas by viewModel.selectedKelas.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    val selectedStudents = remember { mutableStateListOf<Mahasiswa>() }
    val isInSelectionMode = selectedStudents.isNotEmpty()

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = classId) {
        viewModel.getKelasByID(classId)
    }

    LaunchedEffect(key1 = kelas) {
        kelas?.id?.let { id ->
            viewModel.getMahasiswaByKelasID(id)
        }
    }

    if (showDeleteDialog) {
        DeleteDialog(
            onConfirm = {
                viewModel.deleteSelectedStudents(selectedStudents)
                selectedStudents.clear()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
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
            if (isInSelectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${selectedStudents.size} selected", fontWeight = FontWeight.Bold)

                    Row {
                        IconButton(onClick = { selectedStudents.clear() }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }

                        IconButton(onClick = {
                            showDeleteDialog = true

                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    }
                }
            } else {
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
            }

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
            ) {
                items(mahasiswa) { student ->
                    SelectableItemList(
                        data = student,
                        isSelected = selectedStudents.contains(student),
                        onClick = {
                            if (selectedStudents.contains(student)) {
                                selectedStudents.remove(student)
                            } else {
                                selectedStudents.add(student)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun SelectableItemList(
    data: Mahasiswa,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) Color.LightGray else Color.Transparent)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = data.nama,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )
        Text(text = data.nim, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(text = stringResource(id = R.string.student_delete_confirmation)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}