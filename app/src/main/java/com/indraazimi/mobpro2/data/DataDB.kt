package com.indraazimi.mobpro2.data

import androidx.annotation.VisibleForTesting
import com.google.firebase.database.FirebaseDatabase
import com.indraazimi.mobpro2.types.PathConfig

class DataDB(
    private val pathConfig: PathConfig
) {
    private val database = FirebaseDatabase.getInstance()

    val dao = DataDaoImpl(database, pathConfig)

    companion object {
        @Volatile
        var INSTANCE: DataDB? = null

        fun getInstance(pathConfig: PathConfig): DataDB {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = DataDB(pathConfig)
                    INSTANCE = instance
                }

                return instance
            }
        }
    }

    @VisibleForTesting
    fun clear() {
        database.getReference(pathConfig.dosenPath).removeValue()
        database.getReference(pathConfig.kelasPath).removeValue()
        database.getReference(pathConfig.mahasiswaPath).removeValue()
        database.getReference(pathConfig.modulPath).removeValue()
    }
}