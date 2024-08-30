package com.indraazimi.mobpro2mhs.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.indraazimi.mobpro2mhs.data.DataDB
import com.indraazimi.mobpro2mhs.data.DataDao
import com.indraazimi.mobpro2mhs.ui.screen.PROFILE_PHOTO_PATH
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class DataViewModel : ViewModel() {

    private val dataDao: DataDao = DataDB.getInstance().dao
    private val fireBaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _allKelas: MutableStateFlow<List<Kelas>> = MutableStateFlow(emptyList())
    val allKelas: StateFlow<List<Kelas>> = _allKelas.asStateFlow()

    private val _selectedKelas: MutableStateFlow<Kelas?> = MutableStateFlow(null)
    val selectedKelas: StateFlow<Kelas?> = _selectedKelas.asStateFlow()

    private val _selectedMahasiswa: MutableStateFlow<Mahasiswa?> = MutableStateFlow(null)
    val selectedMahasiswa: StateFlow<Mahasiswa?> = _selectedMahasiswa.asStateFlow()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _imageUri: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

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
                _loading.value = false
            }
        }
    }

    fun getMahasiswaByID(id: String) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getMahasiswaByID(id).collect {
                _selectedMahasiswa.value = it
                _loading.value = false
            }
        }
    }

    fun getKelasByMahasiswaID(mahasiswaId: String) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getKelasByMahasiswaID(mahasiswaId).collect {
                _selectedKelas.value = it
                _loading.value = false
            }
        }
    }

    fun getKelasByID(id: String) {
        viewModelScope.launch {
            _loading.value = true
            dataDao.getKelasByID(id).collect {
                _selectedKelas.value = it
                _loading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun uploadImage(capturedImageUri: Uri?, context: Context) {
        viewModelScope.launch {
            _loading.value = true

            val bitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    context.contentResolver,
                    capturedImageUri ?: Uri.EMPTY
                )
            )

            val storageRef = fireBaseStorage.reference.child(PROFILE_PHOTO_PATH)
                .child("${System.currentTimeMillis()}.jpg")

            val baos = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            val data = baos.toByteArray()

            storageRef.putBytes(data).await()

            storageRef.downloadUrl.addOnSuccessListener {
                _imageUri.value = it
                _loading.value = false
            }.await()
        }
    }
}