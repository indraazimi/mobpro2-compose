package com.indraazimi.mobpro2mhs.data

import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import kotlinx.coroutines.flow.Flow

interface DataDao {
    fun addMahasiswa(kelasId: String, mahasiswa: Mahasiswa)

    suspend fun getMahasiswaByID(id: String): Mahasiswa?

    suspend fun getKelasByID(id: String): Kelas?
    fun getAllKelas(): Flow<List<Kelas>>

    suspend fun getKelasByMahasiswaID(mahasiswaId: String): Kelas?
}