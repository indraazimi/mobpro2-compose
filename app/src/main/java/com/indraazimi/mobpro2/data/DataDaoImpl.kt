package com.indraazimi.mobpro2.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.indraazimi.mobpro2.types.PathConfig
import com.indraazimi.mobpro2utils.models.Dosen
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import com.indraazimi.mobpro2utils.models.Modul
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DataDaoImpl(val db: FirebaseDatabase, pathConfig: PathConfig) : DataDao {
    val DOSEN_PATH = pathConfig.dosenPath
    val KELAS_PATH = pathConfig.kelasPath
    val MAHASISWA_PATH = pathConfig.mahasiswaPath
    val MODUL_PATH = pathConfig.modulPath
    val KEY_DOSEN_ID = pathConfig.keyDosenId

    override fun addDosen(dosen: Dosen) {
        db.getReference(DOSEN_PATH).child(dosen.id).setValue(dosen)
    }

    override fun addKelas(dosenId: String, kelas: Kelas) {
        val newClass = db.getReference(KELAS_PATH).push()

        val classId = newClass.key ?: ""

        newClass.setValue(kelas).addOnSuccessListener {
            db.getReference(KELAS_PATH).child(classId).child(DOSEN_PATH).child("dosenId").setValue(dosenId)
        }
    }

    override fun updateKelas(dosenId: String, kelasId: String, kelas: Kelas) {
        db.getReference(KELAS_PATH).child(kelasId).setValue(kelas)
    }

    override fun deleteKelas(kelasId: String) {
        db.getReference(KELAS_PATH).child(kelasId).removeValue()
    }

    override suspend fun getDosenByID(id: String): Flow<Dosen?> = callbackFlow {
        val listener = db.getReference(DOSEN_PATH).child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dosen = snapshot.getValue(Dosen::class.java)?.apply {
                        this.id = snapshot.key ?: ""
                    }
                    trySend(dosen)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { db.getReference(DOSEN_PATH).child(id).removeEventListener(listener) }
    }

    override suspend fun getKelasByDosenID(dosenId: String): Flow<List<Kelas>> = callbackFlow {
        val reference = db.getReference(KELAS_PATH)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val kelasList = mutableListOf<Kelas>()
                for (dataSnapshot in snapshot.children) {
                    val kelas = dataSnapshot.getValue(Kelas::class.java)
                    kelas?.id = dataSnapshot.key ?: ""

                    dataSnapshot.child(DOSEN_PATH).child(KEY_DOSEN_ID).value?.let {
                        if (it == dosenId) {
                            kelasList.add(kelas?.apply {
                                this.id = dataSnapshot.key ?: ""
                            } ?: Kelas())
                        }
                    }

                }
                trySend(kelasList).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList()).isFailure
            }
        }

        reference.addValueEventListener(listener)

        awaitClose { reference.removeEventListener(listener) }
    }

    override suspend fun getKelasByID(kelasId: String): Flow<Kelas?> = callbackFlow {
        val listener = db.getReference(KELAS_PATH).child(kelasId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val kelas = snapshot.getValue(Kelas::class.java)?.apply {
                        this.id = snapshot.key ?: ""
                    }
                    trySend(kelas)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { db.getReference(KELAS_PATH).child(kelasId).removeEventListener(listener) }
    }

    override suspend fun getMahasiswaByKelasID(kelasId: String): Flow<List<Mahasiswa>> = callbackFlow {
        val listener = db.getReference(KELAS_PATH).child(kelasId).child(MAHASISWA_PATH)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = snapshot.children.mapNotNull {
                        it.getValue(Mahasiswa::class.java)?.apply {
                            this.id = it.key ?: ""
                        }
                    }
                    trySend(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { db.getReference(KELAS_PATH).child(kelasId).child(MAHASISWA_PATH).removeEventListener(listener) }
    }

    override fun addModulToKelas(kelasId: String, modul: Modul) {
        db.getReference(KELAS_PATH).child(kelasId).child(MODUL_PATH).push().setValue(modul)
    }

    override suspend fun getModulByKelasID(kelasId: String): Flow<List<Modul>> = callbackFlow {
        val listener = db.getReference(KELAS_PATH).child(kelasId).child(MODUL_PATH)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = snapshot.children.mapNotNull {
                        it.getValue(Modul::class.java)?.apply {
                            this.id = it.key ?: ""
                        }
                    }
                    trySend(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { db.getReference(KELAS_PATH).child(kelasId).child(MODUL_PATH).removeEventListener(listener) }
    }

    override suspend fun getModulByID(modulId: String): Flow<Modul?> = callbackFlow {
        val listener = db.getReference(MODUL_PATH).child(modulId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val modul = snapshot.getValue(Modul::class.java)?.apply {
                        this.id = snapshot.key ?: ""
                    }
                    trySend(modul)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { db.getReference(MODUL_PATH).child(modulId).removeEventListener(listener) }
    }

    override fun updateModul(kelasId: String, modulId: String, modul: Modul) {
        db.getReference(KELAS_PATH).child(kelasId).child(MODUL_PATH).child(modulId).setValue(modul)
    }

    override fun deleteModul(kelasId: String, modules: Set<Modul>) {
        modules.forEach{
            db.getReference(KELAS_PATH).child(kelasId).child(MODUL_PATH).child(it.id).removeValue()
        }
    }
}