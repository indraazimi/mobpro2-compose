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

    private val _selectedDosen: MutableStateFlow<Dosen?> = MutableStateFlow(null)
    val selectedDosen: StateFlow<Dosen?> = _selectedDosen.asStateFlow()

    private val _allKelas: MutableStateFlow<List<Kelas>> = MutableStateFlow(emptyList())
    val allKelas: StateFlow<List<Kelas>> = _allKelas.asStateFlow()

    private val _selectedKelas: MutableStateFlow<Kelas?> = MutableStateFlow(null)
    val selectedKelas: StateFlow<Kelas?> = _selectedKelas.asStateFlow()

    private val _allMahasiswa: MutableStateFlow<List<Mahasiswa>> = MutableStateFlow(emptyList())
    val allMahasiswa: StateFlow<List<Mahasiswa>> = _allMahasiswa.asStateFlow()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun addDosen(dosen: Dosen) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.addDosen(dosen)
            _loading.value = false
        }
    }

    fun getDosenByID(id: String) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getDosenByID(id).collect {
                _selectedDosen.value = it
                _loading.value = false
            }
        }
    }

    fun addKelas(dosenId: String, kelas: Kelas) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.addKelas(dosenId, kelas)
            _loading.value = false
        }
    }

    fun getKelasByID(kelasId: String) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getKelasByID(kelasId).collect {
                _selectedKelas.value = it
                _loading.value = false
            }
        }
    }

    fun getKelasByDosenID(dosenId: String) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getKelasByDosenID(dosenId).collect { kelasList ->
                _allKelas.value = kelasList
                _loading.value = false
            }
        }
    }

    fun getMahasiswaByKelasID(kelasId: String) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getMahasiswaByKelasID(kelasId).collect { mahasiswaList ->
                _allMahasiswa.value = mahasiswaList
                _loading.value = false
            }
        }
    }

    fun deleteSelectedStudents(mahasiswa: List<Mahasiswa>) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.deleteSelectedMahasiswa(selectedKelas.value?.id ?: "", mahasiswa)
            _loading.value = false
        }
    }
}