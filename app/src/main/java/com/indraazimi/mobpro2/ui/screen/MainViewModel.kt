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
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.indraazimi.mobpro2.model.Coordinate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.layout.Forced
import ovh.plrapps.mapcompose.ui.state.MapState
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.pow

class MainViewModel : ViewModel() {

    private val tileStreamProvider = makeTileStreamProvider()

    private val maxLevel = 17
    private val minLevel = 13
    private val tileSize = 256
    private val mapSize = tileSize * 2.0.pow(maxLevel).toInt()

    val mapState = MapState(levelCount = maxLevel + 1, mapSize, mapSize, workerCount = 16) {
        minimumScaleMode(Forced((1 / 2.0.pow(maxLevel - minLevel)).toFloat()))
    }.apply {
        addLayer(tileStreamProvider)
        scale = 2f
    }

    private fun makeTileStreamProvider() = TileStreamProvider { row, col, zoomLvl ->
        try {
            val url = URL("https://tile.openstreetmap.org/$zoomLvl/$col/$row.png")
            val connection = url.openConnection() as HttpURLConnection

            connection.setRequestProperty("User-Agent", "Chrome/120.0.0.0 Safari/537.36")
            connection.doInput = true
            connection.connect()
            BufferedInputStream(connection.inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getCoordinateFrom(result: LocationResult): Coordinate? {
        if (result.locations.isEmpty()) return null
        val location = result.locations.last()
        return Coordinate(location.latitude, location.longitude)
    }

    fun getUserLocation(context: Context): Flow<Coordinate?> = callbackFlow {
        val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(getCoordinateFrom(result))
            }
        }

        try {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (error: SecurityException) {
            Log.d("MainViewModel", "Error: ${error.message}")
        }

        awaitClose { client.removeLocationUpdates(callback) }
    }
}