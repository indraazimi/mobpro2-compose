package com.indraazimi.mobpro2utils.models

import com.google.firebase.database.Exclude

data class Modul(
    @get:Exclude
    var id : String = "",
    val judul : String = "",
    val waktuDitambahkan : Long = System.currentTimeMillis(),
)
