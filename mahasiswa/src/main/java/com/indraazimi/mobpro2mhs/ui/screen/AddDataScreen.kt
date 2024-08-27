package com.indraazimi.mobpro2mhs.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2mhs.R
import com.indraazimi.mobpro2mhs.navigation.Screen
import com.indraazimi.mobpro2mhs.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Mahasiswa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDataScreen(
    navController: NavController,
    user: MutableState<FirebaseUser?>,
    modifier: Modifier = Modifier
) {
    val dataViewModel: DataViewModel = viewModel()

    val classes by dataViewModel.allKelas.collectAsStateWithLifecycle()
    val selectedClass by dataViewModel.selectedKelas.collectAsStateWithLifecycle()
    val loading by dataViewModel.loading.collectAsStateWithLifecycle()

    var selectedClassId by remember { mutableStateOf("") }

    var nameData by remember { mutableStateOf("") }
    var nimData by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedClassName by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        dataViewModel.getAllKelas()
    }

    LaunchedEffect (key1 = selectedClassId.isNotEmpty()) {
        if (loading == false) {
            dataViewModel.getKelasByID(selectedClass?.id ?: "")
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = stringResource(id = R.string.add_data))

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameData,
            onValueChange = { nameData = it },
            label = { Text(stringResource(id = R.string.name)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nimData,
            onValueChange = { nimData = it },
            label = { Text(stringResource(id = R.string.student_id)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedClassName,
                onValueChange = {
                },
                label = { Text(stringResource(id = R.string.select_class)) },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                classes.forEach { kelas ->
                    DropdownMenuItem(
                        text = { Text(kelas.nama) },
                        onClick = {
                            selectedClassId = kelas.id
                            selectedClassName = kelas.nama
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val newMahasiswa = Mahasiswa(
                    id = user.value?.uid ?: "",
                    nama = nameData,
                    nim = nimData
                )

                dataViewModel.addMahasiswa(selectedClassId, newMahasiswa)

                navController.navigate(Screen.Profile.route)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(id = R.string.save))
        }
    }
}

