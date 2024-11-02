/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2s.model

import com.google.firebase.auth.FirebaseUser

data class Mahasiswa(
    val name: String,
    val email: String,
    val photoUrl: String
) {
    // Firestore butuh konstruktor kosong
    constructor() : this("", "", "")

    constructor(user: FirebaseUser) : this(
        user.displayName ?: "",
        user.email ?: "",
        user.photoUrl.toString()
    )

    companion object {
        const val COLLECTION = "mahasiswa"
        const val KELAS_ID = "kelasId"
    }
}