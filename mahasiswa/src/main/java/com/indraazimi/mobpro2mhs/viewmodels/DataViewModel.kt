package com.indraazimi.mobpro2mhs.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indraazimi.mobpro2mhs.data.DataDB
import com.indraazimi.mobpro2mhs.data.DataDao
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DataViewModel() : ViewModel() {

    private val dataDao: DataDao = DataDB.getInstance().dao

    private val _allKelas: MutableStateFlow<List<Kelas>> = MutableStateFlow(emptyList())
    val allKelas: StateFlow<List<Kelas>> = _allKelas.asStateFlow()

    private val _selectedKelas: MutableStateFlow<Kelas?> = MutableStateFlow(null)
    val selectedKelas: StateFlow<Kelas?> = _selectedKelas.asStateFlow()

    private val _allMahasiswa: MutableStateFlow<List<Mahasiswa>> = MutableStateFlow(emptyList())
    val allMahasiswa: StateFlow<List<Mahasiswa>> = _allMahasiswa.asStateFlow()

    private val _selectedMahasiswa: MutableStateFlow<Mahasiswa?> = MutableStateFlow(null)
    val selectedMahasiswa: StateFlow<Mahasiswa?> = _selectedMahasiswa.asStateFlow()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun addMahasiswa(kelasId: String, mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            dataDao.addMahasiswa(kelasId, mahasiswa)
        }
    }

    fun getAllKelas() {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getAllKelas().collect { kelasList ->
                _allKelas.value = kelasList
            }
            _loading.value = false
        }
    }

    fun getMahasiswaByID(id: String) {
        viewModelScope.launch {
            _loading.value = true
            val mahasiswa = dataDao.getMahasiswaByID(id)
            _selectedMahasiswa.value = mahasiswa
            _loading.value = false
        }
    }

    fun getKelasByMahasiswaID(mahasiswaId: String) {
        viewModelScope.launch {
            _loading.value = true
            val kelas = dataDao.getKelasByMahasiswaID(mahasiswaId)
            _selectedKelas.value = kelas
            _loading.value = false
        }
    }

    fun getKelasByID(id: String) {
        viewModelScope.launch {
            _loading.value = true
            val kelas = dataDao.getKelasByID(id)
            _selectedKelas.value = kelas
            _loading.value = false
        }
    }
}