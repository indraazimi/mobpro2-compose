/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2m

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.indraazimi.mobpro2m.ui.screen.app.MahasiswaApp
import com.indraazimi.mobpro2m.ui.theme.Mobpro2Theme
import com.indraazimi.mobpro2s.ui.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobpro2Theme {
                MahasiswaApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MahasiswaAppPreview() {
    Mobpro2Theme {
        WelcomeScreen(
            appLogo = R.mipmap.ic_launcher,
            appName = R.string.app_name
        )
    }
}