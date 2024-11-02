/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2m.ui.screen.main

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.indraazimi.mobpro2s.model.Kelas

class MainViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val dataKelasId = mutableStateListOf<String>()
    val dataKelas = mutableStateListOf<String>()

    fun getDataKelas() {
        db.collection(Kelas.COLLECTION)
            .orderBy(Kelas.NAMA)
            .get()
            .addOnCompleteListener { handleKelas(it) }
    }

    private fun handleKelas(task: Task<QuerySnapshot>) {
        dataKelas.clear()
        dataKelasId.clear()

        if (task.isSuccessful) {
            task.result.documents.forEach { kelas ->
                val nama = kelas.getString(Kelas.NAMA) ?: return@forEach
                dataKelas.add(nama)
                dataKelasId.add(kelas.id)
            }
        }
    }
}