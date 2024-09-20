package com.indraazimi.mobpro2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2.R
import com.indraazimi.mobpro2.navigation.Screen
import com.indraazimi.mobpro2.types.ActionModeState
import com.indraazimi.mobpro2.ui.theme.Mobpro2Theme
import com.indraazimi.mobpro2.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Modul

enum class ProfileScreenMode {
    STUDENT_LIST,
    MODULE_LIST,
    NONE,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, user: MutableState<FirebaseUser?>, modifier: Modifier) {
    var showMenu by remember { mutableStateOf(false) }

    val viewModel: DataViewModel = viewModel()
    val dosen by viewModel.selectedDosen.collectAsStateWithLifecycle()
    val kelas by viewModel.allKelas.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val selectedClass = remember {
        mutableStateOf<Kelas?>(null)
    }

    val studentListMode = remember { mutableStateOf(ProfileScreenMode.NONE) }

    val actionModeState = remember {
        mutableStateOf(ActionModeState.None)
    }

    var showDeleteConfirmationDialog by remember {
        mutableStateOf(false)
    }

    var showUpdateModuleDialog by remember {
        mutableStateOf(false)
    }

    val selectedModules = remember { mutableStateOf(setOf<Modul>()) }

    var showAddModuleDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = user.value?.uid) {
        user.value?.uid?.let { uid ->
            viewModel.getDosenByID(uid)
        }
    }

    LaunchedEffect(key1 = dosen) {
        dosen?.id?.let { id ->
            viewModel.getKelasByDosenID(id)
        }
    }

    Mobpro2Theme {
        Scaffold(
            floatingActionButton = {
                if (studentListMode.value != ProfileScreenMode.STUDENT_LIST) {
                    FloatingActionButton(onClick = {
                        if (studentListMode.value == ProfileScreenMode.MODULE_LIST) {
                            showAddModuleDialog = true
                        } else {
                            navController.navigate(Screen.AddClass.route)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
            },
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
                        when (actionModeState.value) {
                            ActionModeState.MultipleSelection -> {
                                IconButton(onClick = {
                                    showDeleteConfirmationDialog = true
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                                }

                                IconButton(onClick = {
                                    selectedModules.value = emptySet()
                                    actionModeState.value = ActionModeState.None
                                }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                                }
                            }

                            ActionModeState.OneSelection -> {
                                IconButton(onClick = {
                                    val module = selectedModules.value.firstOrNull()
                                    module?.let {
                                        showUpdateModuleDialog = true
                                    }
                                }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                                }

                                IconButton(onClick = {
                                    showDeleteConfirmationDialog = true
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                                }

                                IconButton(onClick = {
                                    selectedModules.value = emptySet()
                                    actionModeState.value = ActionModeState.None
                                }) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                }
                            }

                            else -> {
                                if (selectedClass.value != null) {
                                    when (studentListMode.value) {
                                        ProfileScreenMode.STUDENT_LIST, ProfileScreenMode.MODULE_LIST -> {
                                            IconButton(onClick = {
                                                studentListMode.value = ProfileScreenMode.NONE
                                            }) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null
                                                )
                                            }

                                            IconButton(onClick = {
                                                selectedClass.value = null
                                                studentListMode.value = ProfileScreenMode.NONE
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = null
                                                )
                                            }
                                        }

                                        ProfileScreenMode.NONE -> {
                                            IconButton(onClick = {
                                                selectedClass.value = null
                                                actionModeState.value = ActionModeState.None
                                            }) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                }

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
            if (showUpdateModuleDialog) {
                UpdateModulDialog(
                    oldTitle = selectedModules.value.firstOrNull()?.judul,
                    onDismiss = {
                        showUpdateModuleDialog = false
                    },
                    onSave = {
                        val updatedModul = selectedModules.value.firstOrNull()
                        val newModul = updatedModul?.copy(judul = it)
                        viewModel.updateModul(selectedClass.value?.id ?: "", updatedModul?.id ?: "", newModul ?: Modul())

                        selectedModules.value = emptySet()
                        actionModeState.value = ActionModeState.None

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
                        viewModel.deleteModul(selectedClass.value?.id ?: "", selectedModules.value)
                        selectedModules.value = emptySet()
                        actionModeState.value = ActionModeState.None
                    }
                )
            }

            if (showAddModuleDialog) {
                AddModuleDialog(
                    onDismiss = {
                        showAddModuleDialog = false
                    },
                    onSave = {
                        val newModule = Modul(judul = it)
                        viewModel.addModulToKelas(selectedClass.value?.id ?: "", newModule)
                        showAddModuleDialog = false
                    }
                )
            }

            if (!isTablet && loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {

                Row(
                    modifier = Modifier
                        .padding(paddingValue)
                        .fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        ProfileCard(
                            photoUrl = user.value?.photoUrl.toString(),
                            name = dosen?.nama ?: "",
                            lecturerCode = dosen?.kodeDosen ?: "",
                            email = user.value?.email ?: ""
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(id = R.string.your_class),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 16.dp),
                        ) {
                            items(kelas) {
                                ClassList(kelas = it, selected = selectedClass.value == it) {
                                    if (isTablet) {
                                        selectedClass.value = if (selectedClass.value == it) null else it
                                        actionModeState.value = ActionModeState.None
                                        studentListMode.value = ProfileScreenMode.NONE
                                        selectedModules.value = emptySet()
                                        selectedClass.value = it
                                    } else {
                                        navController.navigate(Screen.ClassMenu.withClassID(it.id))
                                    }
                                }
                            }
                        }
                    }

                    if (isTablet) {
                        if (selectedClass.value != null) {
                            when (studentListMode.value) {
                                ProfileScreenMode.STUDENT_LIST -> {
                                    StudentMenuFragment(
                                        modifier = modifier,
                                        kelas = selectedClass.value,
                                    )
                                }
                                ProfileScreenMode.MODULE_LIST -> {
                                    ModuleMenuFragment(
                                        modifier = modifier,
                                        kelas = selectedClass.value,
                                        selectedModules = selectedModules,
                                        actionModeState = actionModeState,
                                    )
                                }
                                ProfileScreenMode.NONE -> {
                                    ClassMenuListFragment(modifier = modifier, kelas = selectedClass.value, onStudentMenuSelected = {
                                        studentListMode.value = ProfileScreenMode.STUDENT_LIST
                                    }, onModuleMenuSelected = {
                                        studentListMode.value = ProfileScreenMode.MODULE_LIST
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    photoUrl: String?,
    name: String,
    lecturerCode: String,
    email: String
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
                        text = lecturerCode,
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
                text = "$name ($lecturerCode)",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ClassList(
    kelas: Kelas,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                onSelect()
            }
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.Blue else Color.Gray
            ),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = kelas.nama,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}