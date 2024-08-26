package com.indraazimi.mobpro2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indraazimi.mobpro2.data.DataDB
import com.indraazimi.mobpro2.data.DataDao
import com.indraazimi.mobpro2utils.models.Dosen
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DataViewModel() : ViewModel() {

    private val dataDao: DataDao = DataDB.getInstance().dao

    private val _allDosen: MutableStateFlow<List<Dosen>> = MutableStateFlow(emptyList())
    val allDosen: StateFlow<List<Dosen>> = _allDosen.asStateFlow()

    private val _selectedDosen: MutableStateFlow<Dosen?> = MutableStateFlow(null)
    val selectedDosen: StateFlow<Dosen?> = _selectedDosen.asStateFlow()

    private val _allKelas: MutableStateFlow<List<Kelas>> = MutableStateFlow(emptyList())
    val allKelas: StateFlow<List<Kelas>> = _allKelas.asStateFlow()

    private val _selectedKelas: MutableStateFlow<Kelas?> = MutableStateFlow(null)
    val selectedKelas: StateFlow<Kelas?> = _selectedKelas.asStateFlow()

    private val _allMahasiswa: MutableStateFlow<List<Mahasiswa>> = MutableStateFlow(emptyList())
    val allMahasiswa: StateFlow<List<Mahasiswa>> = _allMahasiswa.asStateFlow()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        viewModelScope.launch {
            dataDao.getAllDosen().collect { dosenList ->
                _allDosen.value = dosenList
            }
        }
    }

    fun addDosen(dosen: Dosen) {
        viewModelScope.launch {
            dataDao.addDosen(dosen)
        }
    }

    fun updateDosen(id: String, dosen: Dosen) {
        viewModelScope.launch {
            dataDao.updateDosen(id, dosen)
        }
    }

    fun deleteDosen(ids: List<String>) {
        viewModelScope.launch {
            dataDao.deleteDosen(ids)
        }
    }

    fun getDosenByID(id: String) {
        viewModelScope.launch {
            _loading.value = true
            _selectedDosen.value = dataDao.getDosenByID(id)
            _loading.value = false
        }
    }

    fun getAllKelas(dosenId: String) {
        viewModelScope.launch {
            dataDao.getAllKelas(dosenId).collect { kelasList ->
                _allKelas.value = kelasList
            }
        }
    }

    fun addKelas(dosenId: String, kelas: Kelas) {
        viewModelScope.launch {
            dataDao.addKelas(dosenId, kelas)
        }
    }

    fun updateKelas(dosenId: String, kelasId: String, kelas: Kelas) {
        viewModelScope.launch {
            dataDao.updateKelas(dosenId, kelasId, kelas)
        }
    }

    fun deleteKelas(dosenId: String, kelasId: String) {
        viewModelScope.launch {
            dataDao.deleteKelas(dosenId, kelasId)
        }
    }

    fun getKelasByID(kelasId: String) {
        viewModelScope.launch {
            _selectedKelas.value = dataDao.getKelasByID(kelasId)
        }
    }

    fun getKelasByDosenID(dosenId: String) {
        viewModelScope.launch {
            dataDao.getKelasByDosenID(dosenId).collect { kelasList ->
                _allKelas.value = kelasList
            }
        }
    }

    fun getMahasiswaByKelasID(kelasId: String) {
        viewModelScope.launch {
            dataDao.getMahasiswaByKelasID(kelasId).collect { mahasiswaList ->
                _allMahasiswa.value = mahasiswaList
            }
        }
    }
}