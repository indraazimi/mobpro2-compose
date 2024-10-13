package com.indraazimi.mobpro2mhs.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.indraazimi.mobpro2mhs.viewmodels.DataViewModel
import com.indraazimi.mobpro2utils.models.Modul

@Composable
fun ClassDetailScreen(
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
        modifier = modifier.fillMaxWidth(),
    ) {
        if (loading) {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        } else {

            if (isModuleDetailDialogOpen) {
                ModulDetailDialog(
                    modul = currentModule,
                    onDismiss = {
                        isModuleDetailDialogOpen = false
                    }
                )
            }

            LazyColumn(
            ) {
                items(modul) { item ->
                    ModuleItemList(
                        item = item,
                        isSelected = false,
                        onItemClick = {
                            isModuleDetailDialogOpen = true
                            currentModule = item
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ModuleItemList(
    item: Modul,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
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
fun ModulDetailDialog(
    modul: Modul,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = modul.judul,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
