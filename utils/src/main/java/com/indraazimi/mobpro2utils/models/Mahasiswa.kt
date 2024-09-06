package com.indraazimi.mobpro2utils.models

import com.google.firebase.database.Exclude

data class Mahasiswa(
    @get:Exclude
    var id: String = "",
    val nama: String = "",
    val nim: String = "",
    val fotoProfilUri: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
)