/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2m.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.indraazimi.mobpro2m.MainActivity
import com.indraazimi.mobpro2m.R
import com.indraazimi.mobpro2m.notify.FcmService.Companion.NOTIFICATION_ID

class FcmService : FirebaseMessagingService() {

    companion object {
        const val KEY_URL = "url"
        const val NOTIFICATION_ID = 1
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: return
        val body = message.notification?.body ?: return
        val url = message.data[KEY_URL] ?: return

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            sendNotification(applicationContext, title, body, url)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Token baru: $token")
    }
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
private fun sendNotification(context: Context, title: String, body: String, url: String) {
    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.channel_id)
    )
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(getPendingIntent(context, url))
        .setAutoCancel(true)

    val manager = NotificationManagerCompat.from(context)
    manager.notify(NOTIFICATION_ID, builder.build())
}

private fun getPendingIntent(context: Context, url: String): PendingIntent {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(FcmService.KEY_URL, url)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

    val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    return PendingIntent.getActivity(context, 0, intent, flags)
}

fun createChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
}