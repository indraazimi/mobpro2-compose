package com.indraazimi.mobpro2utils.models

import com.google.firebase.database.Exclude

data class Mahasiswa(
    @get:Exclude
    var id: String = "",
    val nama: String = "",
    val nim: String = "",
    val fotoProfilUri: String = "",
)