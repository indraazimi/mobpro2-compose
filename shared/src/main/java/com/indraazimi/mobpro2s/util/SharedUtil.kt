/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2s.util

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

object SharedUtil {
    fun getUserFlow(scope: CoroutineScope) = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser)
        }
        Firebase.auth.addAuthStateListener(listener)
        awaitClose { Firebase.auth.removeAuthStateListener(listener) }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Firebase.auth.currentUser
    )
}