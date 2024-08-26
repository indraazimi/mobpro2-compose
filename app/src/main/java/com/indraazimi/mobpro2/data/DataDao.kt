package com.indraazimi.mobpro2.data

import com.indraazimi.mobpro2utils.models.Dosen
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import kotlinx.coroutines.flow.Flow

interface DataDao {
    fun addDosen(dosen: Dosen)
    fun updateDosen(id: String, dosen: Dosen)
    fun deleteDosen(ids: List<String>)

    fun addKelas(dosenId: String, kelas: Kelas)
    fun updateKelas(dosenId: String, kelasId: String, kelas: Kelas)
    fun deleteKelas(dosenId: String, kelasId: String)

    suspend fun getAllDosen(): Flow<List<Dosen>>
    suspend fun getDosenByID(id: String): Dosen?

    suspend fun getAllKelas(dosenId: String): Flow<List<Kelas>>
    suspend fun getKelasByID(kelasId: String): Kelas?
    suspend fun getKelasByDosenID(dosenId: String): Flow<List<Kelas>>

    suspend fun getMahasiswaByKelasID(kelasId: String): Flow<List<Mahasiswa>>
}