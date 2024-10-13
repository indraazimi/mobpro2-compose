package com.indraazimi.mobpro2mhs.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.indraazimi.mobpro2mhs.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Modul

@Composable
fun ClassDetailFragment(
    classId: String,
    navController: NavController,
    modifier: Modifier
) {
    val viewModel: DataViewModel = viewModel()

    val kelas by viewModel.selectedKelas.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val modul by viewModel.allModules.collectAsStateWithLifecycle()

    var currentModule by remember {
        mutableStateOf(Modul())
    }

    var isModuleDetailDialogOpen by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = classId) {
        viewModel.getKelasByID(classId)
        viewModel.getModulesByKelasID(classId)
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(500.dp)
            .padding(16.dp)
    ) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = kelas?.nama ?: "",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                if (isModuleDetailDialogOpen) {
                    ModulDetailDialog(
                        modul = currentModule,
                        onDismiss = {
                            isModuleDetailDialogOpen = false
                        }
                    )
                }

                LazyColumn {
                    items(modul) { m ->
                        ModuleItemList(
                            item = m,
                            isSelected = false) {
                                currentModule = m
                                isModuleDetailDialogOpen = true
                        }
                        if (modul.size > 1) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}