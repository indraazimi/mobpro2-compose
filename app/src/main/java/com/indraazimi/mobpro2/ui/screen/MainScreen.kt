/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.indraazimi.mobpro2.R

@Composable
fun MainScreen(
    modifier: Modifier
) {
    val context = LocalContext.current

    var nama  by remember { mutableStateOf("") }
    var umur  by remember { mutableStateOf("") }
    var kategori  by remember { mutableStateOf<Kategori?>(null) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.intro),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text(text = stringResource(R.string.nama)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = umur,
            onValueChange = { umur = it },
            label = { Text(text = stringResource(R.string.umur)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            modifier = Modifier.padding(vertical = 8.dp),
            onClick = {
                if (nama.isEmpty() || umur.isEmpty()) {
                    Toast.makeText(context, R.string.wajib_diisi, Toast.LENGTH_LONG).show()
                } else {
                    kategori = getKategori(umur.toInt())
                }
            }) {
            Text(text = stringResource(R.string.cari_tahu))
        }

        kategori?.let {
            Text(
                text = getMessage(context, nama, it),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

fun getMessage(context: Context, nama: String, kategori: Kategori): String {
    if (kategori == Kategori.INVALID) {
        return context.getString(R.string.tidak_valid)
    }
    return context.getString(R.string.hasil, nama, kategori.toString().uppercase())
}

fun getKategori(umur: Int): Kategori {
    return when {
        umur in 0..1 -> Kategori.BAYI
        umur in 2..5 -> Kategori.BALITA
        umur in 6..13 -> Kategori.ANAK
        umur in 14..21 -> Kategori.REMAJA
        umur in 22..45 -> Kategori.DEWASA
        umur > 45 -> Kategori.LANSIA
        else -> Kategori.INVALID
    }
}

enum class Kategori {
    BAYI,
    BALITA,
    ANAK,
    REMAJA,
    DEWASA,
    LANSIA,
    INVALID
}