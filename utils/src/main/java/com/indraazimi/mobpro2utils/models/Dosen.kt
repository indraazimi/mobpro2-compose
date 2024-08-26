package com.indraazimi.mobpro2utils.models

import com.google.firebase.database.Exclude

data class Dosen(
    @get:Exclude
    var id: String = "",
    val nama: String = "",
    val kodeDosen: String = "",
)