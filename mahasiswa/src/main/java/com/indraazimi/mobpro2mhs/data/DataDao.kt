package com.indraazimi.mobpro2mhs.data

import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import com.indraazimi.mobpro2utils.models.Modul
import kotlinx.coroutines.flow.Flow

interface DataDao {
    fun addMahasiswa(kelasId: String, mahasiswa: Mahasiswa)

    suspend fun getMahasiswaByID(id: String): Flow<Mahasiswa?>

    suspend fun getKelasByID(id: String): Flow<Kelas?>
    fun getAllKelas(): Flow<List<Kelas>>

    suspend fun getKelasByMahasiswaID(mahasiswaId: String): Flow<Kelas?>

    suspend fun getModulesByKelasID(kelasId: String): Flow<List<Modul>>

    suspend fun getModuleByID(modulId: String): Flow<Modul?>
}