package com.indraazimi.mobpro2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indraazimi.mobpro2.data.DataDB
import com.indraazimi.mobpro2.data.DataDao
import com.indraazimi.mobpro2.types.PathConfig
import com.indraazimi.mobpro2.types.ProductionPathConfig
import com.indraazimi.mobpro2utils.models.Dosen
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import com.indraazimi.mobpro2utils.models.Modul
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DataViewModel(
    pathConfig: PathConfig = ProductionPathConfig()
) : ViewModel() {

    private val dataDao: DataDao = DataDB.getInstance(
        pathConfig = pathConfig
    ).dao

    private val _selectedDosen: MutableStateFlow<Dosen?> = MutableStateFlow(null)
    val selectedDosen: StateFlow<Dosen?> = _selectedDosen.asStateFlow()

    private val _allKelas: MutableStateFlow<List<Kelas>> = MutableStateFlow(emptyList())
    val allKelas: StateFlow<List<Kelas>> = _allKelas.asStateFlow()

    private val _selectedKelas: MutableStateFlow<Kelas?> = MutableStateFlow(null)
    val selectedKelas: StateFlow<Kelas?> = _selectedKelas.asStateFlow()

    private val _allMahasiswa: MutableStateFlow<List<Mahasiswa>> = MutableStateFlow(emptyList())
    val allMahasiswa: StateFlow<List<Mahasiswa>> = _allMahasiswa.asStateFlow()

    private val _allModul: MutableStateFlow<List<Modul>> = MutableStateFlow(emptyList())
    val allModul: StateFlow<List<Modul>> = _allModul.asStateFlow()

    private val _selectedModul: MutableStateFlow<Modul?> = MutableStateFlow(null)
    val selectedModul: StateFlow<Modul?> = _selectedModul.asStateFlow()

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

    fun addModulToKelas(kelasId: String, modul: Modul) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.addModulToKelas(kelasId, modul)
            _loading.value = false
        }
    }

    fun getModulByKelasID(kelasId: String) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getModulByKelasID(kelasId).collect { modulList ->
                _allModul.value = modulList
                _loading.value = false
            }
        }
    }

    fun getModulByID(modulId: String) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getModulByID(modulId).collect { modul ->
                _selectedModul.value = modul
                _loading.value = false
            }
        }
    }

    fun updateModul(kelasId: String, modulId: String, modul: Modul) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.updateModul(kelasId, modulId, modul)
            _loading.value = false
        }
    }

    fun deleteModul(kelasId: String, modules: Set<Modul>) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.deleteModul(kelasId, modules)
            _loading.value = false
        }
    }
}