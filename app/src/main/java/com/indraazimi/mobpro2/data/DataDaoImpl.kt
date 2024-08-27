package com.indraazimi.mobpro2.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.indraazimi.mobpro2.data.DataDB.Companion.DOSEN_PATH
import com.indraazimi.mobpro2.data.DataDB.Companion.KELAS_PATH
import com.indraazimi.mobpro2.data.DataDB.Companion.KEY_DOSEN_ID
import com.indraazimi.mobpro2.data.DataDB.Companion.MAHASISWA_PATH
import com.indraazimi.mobpro2utils.models.Dosen
import com.indraazimi.mobpro2utils.models.Kelas
import com.indraazimi.mobpro2utils.models.Mahasiswa
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DataDaoImpl(val db: FirebaseDatabase) : DataDao {
    override fun addDosen(dosen: Dosen) {
        db.getReference(DOSEN_PATH).child(dosen.id).setValue(dosen)
    }

    override fun updateDosen(id: String, dosen: Dosen) {
        db.getReference(DOSEN_PATH).child(id).setValue(dosen)
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

    override suspend fun getDosenByID(id: String): Dosen? = suspendCancellableCoroutine { cont ->
        db.getReference(DOSEN_PATH).child(id).get().addOnSuccessListener { snapshot ->
            val dosen = snapshot.getValue(Dosen::class.java)?.apply {
                this.id = snapshot.key ?: ""
            }
            cont.resume(dosen)
        }.addOnFailureListener {
            cont.resume(null)
        }
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
                            kelasList.add(kelas!!)
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

    override suspend fun getKelasByID(kelasId: String): Kelas? = suspendCancellableCoroutine { cont ->
        db.getReference(KELAS_PATH).child(kelasId).get().addOnSuccessListener { snapshot ->
            val kelas = snapshot.getValue(Kelas::class.java)?.apply {
                this.id = snapshot.key ?: ""
            }
            cont.resume(kelas)
        }.addOnFailureListener {
            cont.resume(null)
        }
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
}