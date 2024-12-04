package com.indraazimi.mobpro2.ui.screen

import org.junit.Assert.assertEquals
import org.junit.Test

class MainScreenTest {

    @Test
    fun getKategori() {
        val anak = getKategori(umur = 7)
        assertEquals(Kategori.ANAK, anak)

        val remaja = getKategori(umur = 17)
        assertEquals(Kategori.REMAJA, remaja)

        val invalid = getKategori(umur = -1)
        assertEquals(Kategori.INVALID, invalid)
    }
}