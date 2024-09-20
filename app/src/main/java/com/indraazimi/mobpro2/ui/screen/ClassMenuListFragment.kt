package com.indraazimi.mobpro2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.indraazimi.mobpro2.types.ActionModeState
import com.indraazimi.mobpro2.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import com.indraazimi.mobpro2utils.models.Modul

@Composable
fun ClassMenuListFragment(
    modifier: Modifier = Modifier,
    kelas: Kelas?,
    onStudentMenuSelected: () -> Unit,
    onModuleMenuSelected: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(500.dp)
            .padding(16.dp)
    ) {
        Text(
            text = kelas?.nama ?: "",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                onStudentMenuSelected()
            }) {
                Text("Student Menu")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                onModuleMenuSelected()
            }) {
                Text("Module Menu")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StudentMenuFragment(
    kelas: Kelas?,
    modifier: Modifier = Modifier,
) {
    val viewModel: DataViewModel = viewModel()

    val mahasiswa by viewModel.allMahasiswa.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    LaunchedEffect(kelas?.id) {
        kelas?.let {
            viewModel.getMahasiswaByKelasID(it.id)
        }
    }

    Column(modifier = modifier.width(500.dp)) {
        Text(
            text = kelas?.nama ?: "",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
            ) {
                items(
                    items = mahasiswa,
                    key = { it.nim }
                ) { mahasiswaItem ->
                    StudentItemListFrag(data = mahasiswaItem, modifier)
                    HorizontalDivider()
                }
            }
        }

    }
}

@Composable
fun StudentItemListFrag(data: Mahasiswa, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = data.fotoProfilUri,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = data.nama,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = data.nim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ModuleMenuFragment(
    kelas: Kelas?,
    modifier: Modifier,
    selectedModules: MutableState<Set<Modul>>,
    actionModeState: MutableState<ActionModeState>,
) {
    val viewModel: DataViewModel = viewModel()

    val modules by viewModel.allModul.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(kelas?.id) {
        kelas?.let {
            viewModel.getModulByKelasID(it.id)
        }
    }

    Column(modifier = modifier.width(500.dp)) {
        Text(
            text = kelas?.nama ?: "",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = modules,
                    key = {
                        it.id
                    }
                ) { module ->
                    ModuleItemListFrag(
                        data = module,
                        isSelected = selectedModules.value.map { it.id }.contains(module.id),
                        onItemClick = {
                            if (actionModeState.value != ActionModeState.None) {
                                selectedModules.value = if (selectedModules.value.contains(module)) {
                                    selectedModules.value - module
                                } else {
                                    selectedModules.value + module
                                }
                                actionModeState.value = when {
                                    selectedModules.value.isEmpty() -> ActionModeState.None
                                    selectedModules.value.size == 1 -> ActionModeState.OneSelection
                                    else -> ActionModeState.MultipleSelection
                                }
                            }
                        },
                        onItemLongPress = {
                            actionModeState.value = if (selectedModules.value.isEmpty()) {
                                ActionModeState.OneSelection
                            } else {
                                ActionModeState.MultipleSelection
                            }
                            selectedModules.value += module
                        },
                        modifier = modifier
                    )
                    HorizontalDivider()
                }
            }

            if (showDeleteConfirmationDialog) {
                DeleteConfirmationDialog(
                    onDismiss = {
                        showDeleteConfirmationDialog = false
                    },
                    onDelete = {
                        viewModel.deleteModul(kelas?.id ?: "", selectedModules.value)
                        selectedModules.value = emptySet()
                        actionModeState.value = ActionModeState.None
                    }
                )
            }
        }
    }
}

@Composable
fun ModuleItemListFrag(
    data: Modul,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    onItemLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onItemLongPress()
                    },
                    onTap = {
                        onItemClick()
                    }
                )
            }
            .background(if (isSelected) Color.Gray else Color.Transparent),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = data.judul,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

