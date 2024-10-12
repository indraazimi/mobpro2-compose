/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2m.ui.screen.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.indraazimi.mobpro2m.ui.screen.main.MainScreen
import com.indraazimi.mobpro2m.R
import com.indraazimi.mobpro2s.ui.WelcomeScreen

@Composable
fun MahasiswaApp() {
    val viewModel: AppViewModel = viewModel()
    val userFlow by viewModel.userFlow.collectAsState()

    if (userFlow == null) {
        Scaffold { innerPadding ->
            WelcomeScreen(
                appLogo = R.mipmap.ic_launcher,
                appName = R.string.app_name,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    userFlow?.let { MainScreen(it) }
}