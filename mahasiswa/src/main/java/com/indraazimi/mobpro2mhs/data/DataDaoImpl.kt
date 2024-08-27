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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DataDaoImpl(val db: FirebaseDatabase) : DataDao {
    override fun addMahasiswa(kelasId: String, mahasiswa: Mahasiswa) {
        db.getReference(KELAS_PATH).child(kelasId).child(MAHASISWA_PATH).child(mahasiswa.id).setValue(mahasiswa)
    }

    override fun updateMahasiswa(id: String, mahasiswa: Mahasiswa) {
        db.getReference(MAHASISWA_PATH).child(id).setValue(mahasiswa)
    }

    override fun deleteMahasiswa(ids: List<String>) {
        ids.forEach { db.getReference(MAHASISWA_PATH).child(it).removeValue() }
    }

    override suspend fun getMahasiswaByID(id: String): Mahasiswa? = suspendCancellableCoroutine { cont ->
        val reference = db.getReference(KELAS_PATH)

        reference.get().addOnSuccessListener { snapshot ->
            var mahasiswa: Mahasiswa? = null
            snapshot.children.forEach() {
                mahasiswa = it.child(MAHASISWA_PATH).child(id).getValue(Mahasiswa::class.java)
                mahasiswa?.id = it.key ?: ""
            }
            cont.resume(mahasiswa)

        }.addOnFailureListener {
            cont.resume(null)
        }
    }

    override fun getAllMahasiswa(): Flow<List<Mahasiswa>> {
        return callbackFlow {
            val listener = db.getReference(MAHASISWA_PATH).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = snapshot.children.mapNotNull { it.getValue(Mahasiswa::class.java) }
                    trySend(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

            awaitClose { db.getReference(MAHASISWA_PATH).removeEventListener(listener) }
        }
    }

    override suspend fun getKelasByID(id: String): Kelas? = suspendCancellableCoroutine { cont ->
        db.getReference(KELAS_PATH).child(id).get().addOnSuccessListener { snapshot ->
            val kelas = snapshot.getValue(Kelas::class.java)?.apply {
                this.id = snapshot.key ?: ""
            }
            cont.resume(kelas)
        }.addOnFailureListener { exception ->
            cont.resumeWithException(exception)
        }
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

    override suspend fun getKelasByMahasiswaID(mahasiswaId: String): Kelas? = suspendCancellableCoroutine { cont ->
        db.getReference(KELAS_PATH).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                var foundKelas: Kelas? = null

                for (kelasSnapshot in snapshot.children) {
                    val mahasiswaSnapshot = kelasSnapshot.child(MAHASISWA_PATH).child(mahasiswaId)

                    if (mahasiswaSnapshot.exists()) {
                        foundKelas = kelasSnapshot.getValue(Kelas::class.java)
                        break
                    }
                }

                if (foundKelas != null) {
                    cont.resume(foundKelas)
                } else {
                    cont.resume(null)
                }
            } else {
                cont.resumeWithException(task.exception ?: Exception("Unknown error occurred"))
            }
        }
    }
}