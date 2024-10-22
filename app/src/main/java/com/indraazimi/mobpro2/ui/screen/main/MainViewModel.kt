/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2.ui.screen.main

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.indraazimi.mobpro2s.model.Kelas

class MainViewModel(private val uid: String) : ViewModel() {

    private val db = Firebase.firestore

    var data = mutableStateListOf<Kelas>()
        private set

    private var registration: ListenerRegistration? = null

    private val listener = object : EventListener<QuerySnapshot> {
        override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
            if (error != null) {
                Log.e("MainViewModel", "onEvent:error", error)
                return
            }

            value?.documentChanges?.forEach { handle(it) }
        }
    }

    private fun handle(change: DocumentChange) {
        when (change.type) {
            DocumentChange.Type.ADDED -> {
                val kelas = change.document.toObject<Kelas>()
                data.add(change.newIndex, kelas)
            }
            DocumentChange.Type.MODIFIED -> {
                val kelas = change.document.toObject<Kelas>()
                if (change.oldIndex == change.newIndex) {
                    data[change.oldIndex] = kelas
                } else {
                    data.removeAt(change.oldIndex)
                    data.add(change.newIndex, kelas)
                }
            }
            DocumentChange.Type.REMOVED -> {
                data.removeAt(change.oldIndex)
            }
        }
    }

    init {
        registration = db.collection(Kelas.COLLECTION)
            .addSnapshotListener(listener)
    }

    fun insert(nama: String) {
        db.collection(Kelas.COLLECTION).add(Kelas(uid, nama))
    }

    override fun onCleared() {
        registration?.remove()
        registration = null
        super.onCleared()
    }
}