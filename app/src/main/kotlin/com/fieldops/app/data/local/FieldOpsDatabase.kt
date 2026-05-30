package com.fieldops.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fieldops.app.data.local.dao.AssetDao
import com.fieldops.app.data.local.dao.IncidentDao
import com.fieldops.app.data.local.entity.AssetEntity
import com.fieldops.app.data.local.entity.IncidentEntity

@Database(
    entities = [IncidentEntity::class, AssetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FieldOpsDatabase : RoomDatabase() {

    abstract fun incidentDao(): IncidentDao
    abstract fun assetDao(): AssetDao

    companion object {
        @Volatile
        private var INSTANCE: FieldOpsDatabase? = null

        fun getDatabase(context: Context): FieldOpsDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FieldOpsDatabase::class.java,
                    "fieldops_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
