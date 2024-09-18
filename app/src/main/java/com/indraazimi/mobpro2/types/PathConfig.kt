package com.indraazimi.mobpro2.types

interface PathConfig {
    val dosenPath: String
    val kelasPath: String
    val mahasiswaPath: String
    val keyDosenId: String
    val modulPath: String
}

class ProductionPathConfig : PathConfig {
    override val dosenPath = "dosen"
    override val kelasPath = "kelas"
    override val mahasiswaPath = "mahasiswa"
    override val keyDosenId = "dosenId"
    override val modulPath = "modul"
}

class TestingPathConfig : PathConfig {
    override val dosenPath = "test_dosen"
    override val kelasPath = "test_kelas"
    override val mahasiswaPath = "test_mahasiswa"
    override val keyDosenId = "dosenId"
    override val modulPath = "test_modul"
}