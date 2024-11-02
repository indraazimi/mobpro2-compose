/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2m.ui.screen.main

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2m.R
import com.indraazimi.mobpro2s.ui.AppBarWithLogout
import com.indraazimi.mobpro2s.ui.UserProfileCard

@Composable
fun MainScreen(
    user: FirebaseUser
) {
    val viewModel: MainViewModel = viewModel()

    LaunchedEffect(true) {
        viewModel.getDataKelas()
    }

    Scaffold(
        topBar = { AppBarWithLogout(R.string.app_name) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            UserProfileCard(user)

            if (viewModel.dataKelas.isNotEmpty()) {
                PilihKelas(viewModel.dataKelas) {
                    Log.d("MainScreen", "Item terpilih: $it")
                }
            }
        }
    }
}

@Composable
fun PilihKelas(
    data: List<String>,
    onConfirmation: (Int) -> Unit
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.pilih_kelas),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        DropDownKelas(data, selectedItem) {
            selectedItem = it
        }
        Button(
            onClick = { onConfirmation(selectedItem) },
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = stringResource(R.string.simpan))
        }
    }
}

@Composable
fun DropDownKelas(
    data: List<String>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit
) {
    var dropdownSize by remember { mutableStateOf(Size.Zero)}
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .onGloballyPositioned { dropdownSize = it.size.toSize() }
                .border(
                    border = BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { isExpanded = true }
        ) {
            Text(
                text = data[selectedItem],
                modifier = Modifier.padding(16.dp)
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.width(
                with(LocalDensity.current) { dropdownSize.width.toDp() }
            )
        ) {
            data.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = { Text(text = label) },
                    onClick = {
                        isExpanded = false
                        onItemClick(index)
                    }
                )
            }
        }
    }
}