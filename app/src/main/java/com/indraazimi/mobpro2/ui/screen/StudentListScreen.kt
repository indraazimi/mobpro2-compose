package com.indraazimi.mobpro2.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2.R
import com.indraazimi.mobpro2.navigation.Screen
import com.indraazimi.mobpro2.ui.theme.Mobpro2Theme
import com.indraazimi.mobpro2.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Mahasiswa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(classId: String, modifier: Modifier, user: MutableState<FirebaseUser?>, navController: NavController) {
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

    var showMenu by remember { mutableStateOf(false) }

    Mobpro2Theme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.student_list))
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    actions = {
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
                    },
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) { paddingValue ->
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
                            .padding(paddingValue),
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
                            StudentItemList(data = it)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentItemList(data: Mahasiswa) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = data.fotoProfilUri,
            onLoading = {

            },
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(),
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
