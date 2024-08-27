package com.indraazimi.mobpro2mhs.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.indraazimi.mobpro2mhs.data.DataDB.Companion.KELAS_PATH
import com.indraazimi.mobpro2mhs.data.DataDB.Companion.MAHASISWA_PATH
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DataDaoImpl(val db: FirebaseDatabase) : DataDao {
    override fun addMahasiswa(kelasId: String, mahasiswa: Mahasiswa) {
        db.getReference(KELAS_PATH).child(kelasId).child(MAHASISWA_PATH).child(mahasiswa.id).setValue(mahasiswa)
    }

    override suspend fun getMahasiswaByID(id: String): Flow<Mahasiswa?> = callbackFlow {
        val listener = db.getReference(KELAS_PATH)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mahasiswa = snapshot.children.mapNotNull { kelasSnapshot ->
                        kelasSnapshot.child(MAHASISWA_PATH).children.mapNotNull { mahasiswaSnapshot ->
                            if (mahasiswaSnapshot.key == id) {
                                mahasiswaSnapshot.getValue(Mahasiswa::class.java)?.apply {
                                    this.id = mahasiswaSnapshot.key ?: ""
                                }
                            } else {
                                null
                            }
                        }
                    }.flatten().firstOrNull()
                    trySend(mahasiswa)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose { db.getReference(KELAS_PATH).removeEventListener(listener) }
    }

    override suspend fun getKelasByID(id: String): Flow<Kelas?> = callbackFlow {
        val listener = db.getReference(KELAS_PATH).child(id)
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
        awaitClose { db.getReference(KELAS_PATH).child(id).removeEventListener(listener) }
    }

    override fun getAllKelas(): Flow<List<Kelas>> {
        return callbackFlow {
            val listener = db.getReference(KELAS_PATH).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = snapshot.children.mapNotNull {
                        val data = it.getValue(Kelas::class.java)
                        data?.id = it.key ?: ""
                        data
                    }
                    Log.d("DataDaoImpl", "getAllKelas: $dataList")
                    trySend(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

            awaitClose { db.getReference(KELAS_PATH).removeEventListener(listener) }
        }
    }

    override suspend fun getKelasByMahasiswaID(mahasiswaId: String): Flow<Kelas?> = callbackFlow {
        val listener = db.getReference(KELAS_PATH)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val kelas = snapshot.children.mapNotNull { kelasSnapshot ->
                        kelasSnapshot.child(MAHASISWA_PATH).children.mapNotNull { mahasiswaSnapshot ->
                            if (mahasiswaSnapshot.key == mahasiswaId) {
                                kelasSnapshot.getValue(Kelas::class.java)?.apply {
                                    this.id = kelasSnapshot.key ?: ""
                                }
                            } else {
                                null
                            }
                        }
                    }.flatten().firstOrNull()
                    trySend(kelas)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose { db.getReference(KELAS_PATH).removeEventListener(listener) }
    }
}