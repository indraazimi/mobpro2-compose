package com.indraazimi.mobpro2.data

import com.google.firebase.database.FirebaseDatabase

class DataDB {
    private val database = FirebaseDatabase.getInstance()

    val dao = DataDaoImpl(database)

    companion object {
        const val DOSEN_PATH = "dosen"
        const val KELAS_PATH = "kelas"
        const val MAHASISWA_PATH = "mahasiswa"

        @Volatile
        var INSTANCE: DataDB? = null

        fun getInstance(): DataDB {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = DataDB()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}