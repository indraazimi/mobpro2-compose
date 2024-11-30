/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2.model

import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sinh
import kotlin.math.tan

data class Coordinate(
    val latitude: Double,
    val longitude: Double
) {
    val xScaled = (longitude + 180.0) / 360.0
    private val lRadian = Math.toRadians(latitude)
    val yScaled = (1.0 - ln(tan(lRadian) + 1 / cos(lRadian)) / Math.PI) / 2.0

    companion object {
        fun from(xScaled: Double, yScaled: Double): Coordinate {
            val longitude = xScaled * 360.0 - 180.0
            val lRadian = atan(sinh(Math.PI * (1 - 2 * yScaled)))
            val latitude = Math.toDegrees(lRadian)
            return Coordinate(latitude, longitude)
        }
    }
}