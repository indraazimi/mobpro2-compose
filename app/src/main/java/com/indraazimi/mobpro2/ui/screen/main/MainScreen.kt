/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2.ui.screen.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2.R
import com.indraazimi.mobpro2.navigation.Screen
import com.indraazimi.mobpro2.ui.screen.detail.DetailScreenContent
import com.indraazimi.mobpro2s.model.Kelas
import com.indraazimi.mobpro2s.ui.AppBarWithLogout
import com.indraazimi.mobpro2s.ui.UserProfileCard

@Composable
fun MainScreen(
    navController: NavHostController,
    user: FirebaseUser
) {
    val factory = ViewModelFactory(user.uid)
    val viewModel: MainViewModel = viewModel(factory = factory)

    val configuration = LocalConfiguration.current
    val isTwoPane = configuration.smallestScreenWidthDp >= 600
    val uiState = if (isTwoPane)
        MainUiState((-44).dp, FabPosition.Center)
    else
        MainUiState(0.dp, FabPosition.End)

    var selectedId by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppBarWithLogout(R.string.app_name) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.offset(x = uiState.fabOffset)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.tambah_kelas),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        floatingActionButtonPosition = uiState.fabPosition
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            MainScreenContent(
                user = user,
                data = viewModel.data,
                selectedIndex = viewModel.dataId.indexOf(selectedId),
                modifier = Modifier.weight(1f)
            ) {
                if (isTwoPane) {
                    selectedId = viewModel.dataId[it]
                }
                else {
                    val route = Screen.Detail.withData(
                        viewModel.dataId[it],
                        viewModel.data[it].nama
                    )
                    navController.navigate(route)
                }
            }

            if (isTwoPane) {
                VerticalDivider(
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (selectedId == null) {
                    Box(Modifier.weight(1f))
                }

                selectedId?.let {
                    DetailScreenContent(it, Modifier.weight(1f))
                }
            }
        }

        if (showDialog) {
            KelasDialog(onDismissRequest = { showDialog = false }) {
                viewModel.insert(it)
                showDialog = false
            }
        }
    }
}

@Composable
fun MainScreenContent(
    user: FirebaseUser,
    data: List<Kelas>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onKelasClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 84.dp)
    ) {
        item {
            UserProfileCard(
                user = user,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        itemsIndexed(data) { index, kelas ->
            val background = if (selectedIndex == index)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant

            Card(
                colors = CardDefaults.cardColors(containerColor = background),
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .clickable { onKelasClick(index) }
            ) {
                Text(
                    text = kelas.nama,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                )
            }
        }
    }
}

class MainUiState(
    val fabOffset: Dp,
    val fabPosition: FabPosition
)