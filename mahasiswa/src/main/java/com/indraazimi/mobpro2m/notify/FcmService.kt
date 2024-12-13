/*
 * Copyright (c) 2024-2026 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2m.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import com.indraazimi.mobpro2m.R

fun createChannel(context: Context) {
    val notificationChannel = NotificationChannel(
        context.getString(R.string.channel_id),
        context.getString(R.string.channel_name),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        setShowBadge(false)
        enableLights(true)
        lightColor = Color.RED
        enableVibration(true)
        description = context.getString(R.string.channel_desc)
    }

    val manager = context.getSystemService(NotificationManager::class.java)
    manager?.createNotificationChannel(notificationChannel)
}