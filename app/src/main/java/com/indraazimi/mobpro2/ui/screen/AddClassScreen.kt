package com.indraazimi.mobpro2.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2.R
import com.indraazimi.mobpro2.navigation.Screen
import com.indraazimi.mobpro2.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Kelas

@Composable
fun AddClassScreen(navController: NavController, user: MutableState<FirebaseUser?>, modifier: Modifier) {
    val viewModel: DataViewModel = viewModel()

    var nameData by remember { mutableStateOf("") }

    if (user.value == null) {
        navController.navigate(Screen.Login.route)
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = stringResource(id = R.string.add_class))

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameData,
            onValueChange = {
                if (it.length <= 25) {
                    nameData = it
                }
            },
            label = { Text(stringResource(id = R.string.name)) },
            maxLines = 1,
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val newKelas = Kelas(
                    nama = nameData,
                )

                viewModel.addKelas(
                    user.value?.uid ?: "",
                    newKelas
                )

                navController.popBackStack()
            }
        ) {
            Text(stringResource(id = R.string.save))
        }
    }
}