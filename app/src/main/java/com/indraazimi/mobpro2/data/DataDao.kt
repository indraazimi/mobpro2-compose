package com.indraazimi.mobpro2.data

import com.indraazimi.mobpro2utils.models.Dosen
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import com.indraazimi.mobpro2utils.models.Modul
import kotlinx.coroutines.flow.Flow

interface DataDao {
    fun addDosen(dosen: Dosen)

    fun addKelas(dosenId: String, kelas: Kelas)
    fun updateKelas(dosenId: String, kelasId: String, kelas: Kelas)
    fun deleteKelas(kelasId: String)

    fun addModulToKelas(kelasId: String, modul: Modul)
    fun updateModul(kelasId: String, modulId: String, modul: Modul)
    fun deleteModul(kelasId: String, modulIds: Set<Modul>)

    suspend fun getDosenByID(id: String): Flow<Dosen?>

    suspend fun getKelasByID(kelasId: String): Flow<Kelas?>
    suspend fun getKelasByDosenID(dosenId: String): Flow<List<Kelas>>

    suspend fun getMahasiswaByKelasID(kelasId: String): Flow<List<Mahasiswa>>

    suspend fun getModulByKelasID(kelasId: String): Flow<List<Modul>>
    suspend fun getModulByID(modulId: String): Flow<Modul?>
}