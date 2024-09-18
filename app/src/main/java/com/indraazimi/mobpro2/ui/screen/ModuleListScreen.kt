package com.indraazimi.mobpro2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2.R
import com.indraazimi.mobpro2.navigation.Screen
import com.indraazimi.mobpro2.types.ActionModeState
import com.indraazimi.mobpro2.ui.theme.Mobpro2Theme
import com.indraazimi.mobpro2.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Modul

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleListScreen(classId: String, modifier: Modifier, user: MutableState<FirebaseUser?>, navController: NavController) {
    var showMenu by remember { mutableStateOf(false) }
    var showAddModuleDialog by remember { mutableStateOf(false) }
    var showUpdateModuleDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val viewModel: DataViewModel = viewModel()
    val kelas by viewModel.selectedKelas.collectAsStateWithLifecycle()
    val modules by viewModel.allModul.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    var selectedModules by remember { mutableStateOf(setOf<Modul>()) }

    var actionModeState by remember { mutableStateOf(ActionModeState.None) }

    LaunchedEffect(key1 = classId) {
        viewModel.getKelasByID(classId)
    }

    LaunchedEffect(key1 = modules, key2 = kelas) {
        viewModel.selectedKelas.value?.id?.let { id ->
            viewModel.getModulByKelasID(id)
        }
    }

    Mobpro2Theme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    showAddModuleDialog = true
                }, modifier = Modifier.testTag("add_module_fab")) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.module_list))
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    actions = {
                        when (actionModeState) {
                            ActionModeState.MultipleSelection -> {
                                IconButton(onClick = {
                                    showDeleteConfirmationDialog = true
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                                }

                                IconButton(onClick = {
                                    selectedModules = emptySet()
                                    actionModeState = ActionModeState.None
                                }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                                }
                            }

                            ActionModeState.OneSelection -> {
                                IconButton(onClick = {
                                    val module = selectedModules.firstOrNull()
                                    module?.let {
                                        showUpdateModuleDialog = true
                                    }
                                }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                                }

                                IconButton(onClick = {
                                    showDeleteConfirmationDialog = true
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                                }

                                IconButton(onClick = {
                                    selectedModules = emptySet()
                                    actionModeState = ActionModeState.None
                                }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                                }
                            }

                            else -> {
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
                                                showMenu = false
                                            },
                                            text = {
                                                Text(text = stringResource(R.string.logout))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    },
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) { paddingValue ->

            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            if (showAddModuleDialog) {
                AddModuleDialog(
                    onDismiss = {
                        showAddModuleDialog = false
                    },
                    onSave = {
                        val newModule = Modul(judul = it)
                        viewModel.addModulToKelas(classId, newModule)
                        showAddModuleDialog = false
                    }
                )
            }

            if (showUpdateModuleDialog) {
                UpdateModulDialog(
                    oldTitle = selectedModules.firstOrNull()?.judul,
                    onDismiss = {
                        showUpdateModuleDialog = false
                    },
                    onSave = {
                        val updatedModul = selectedModules.first()
                        val newModul = updatedModul.copy(judul = it)
                        viewModel.updateModul(classId, updatedModul.id, newModul)

                        selectedModules = emptySet()
                        actionModeState = ActionModeState.None

                        showUpdateModuleDialog = false
                    }
                )
            }

            if (showDeleteConfirmationDialog) {
                DeleteConfirmationDialog(
                    onDismiss = {
                        showDeleteConfirmationDialog = false
                    },
                    onDelete = {
                        viewModel.deleteModul(classId, selectedModules)
                        selectedModules = emptySet()
                        actionModeState = ActionModeState.None
                    }
                )
            }

            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValue),
                    contentAlignment  = Alignment.Center
                ) {
                    Text(
                        text = kelas?.nama ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = modifier.fillMaxSize().testTag("module_list"),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(modules) { module ->
                        ModuleItemList(
                            item = module,
                            isSelected = selectedModules.map {
                                it.id
                            }.contains(module.id),
                            onItemLongPress = {
                                actionModeState = if (selectedModules.isEmpty()) {
                                    ActionModeState.OneSelection
                                } else {
                                    ActionModeState.MultipleSelection
                                }
                                selectedModules = selectedModules + module
                            },
                            onItemClick = {
                                if (actionModeState != ActionModeState.None) {
                                    selectedModules = if (selectedModules.contains(module)) {
                                        selectedModules - module
                                    } else {
                                        selectedModules + module
                                    }
                                    actionModeState = when {
                                        selectedModules.isEmpty() -> ActionModeState.None
                                        selectedModules.size == 1 -> ActionModeState.OneSelection
                                        else -> ActionModeState.MultipleSelection
                                    }
                                }
                            }
                        )

                        if (modules.last() != module) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleItemList(
    item: Modul,
    isSelected: Boolean,
    onItemLongPress: () -> Unit,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = item.judul,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = stringResource(id = R.string.delete_confirmation), style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    TextButton(onClick = {
                        onDelete()
                        onDismiss()
                    }) {
                        Text(text = stringResource(id = R.string.delete))
                    }
                }
            }
        }
    }
}