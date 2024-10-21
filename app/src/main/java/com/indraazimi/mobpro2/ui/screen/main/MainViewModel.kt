/*
 * Copyright (c) 2024-2026 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2.ui.screen.main

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.indraazimi.mobpro2s.model.Kelas

class MainViewModel : ViewModel() {

    private val db = Firebase.firestore

    private var uid: String = ""

    fun init(userId: String) {
        if (userId == uid) return
        uid = userId
    }

    fun insert(nama: String) {
        db.collection(Kelas.COLLECTION).add(Kelas(uid, nama))
    }
}