/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2s.model

data class Kelas(
    val dosenId: String,
    val nama: String
) {
    // Firestore butuh konstruktor kosong
    constructor() : this("", "")

    companion object {
        const val COLLECTION = "kelas"
        const val DOSEN_ID = "dosenId"
        const val NAMA = "nama"
    }
}