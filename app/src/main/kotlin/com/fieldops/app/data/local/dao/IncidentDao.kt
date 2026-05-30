package com.fieldops.app.data.local.dao

import androidx.room.*
import com.fieldops.app.data.local.entity.IncidentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {

    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE is_synced = 0")
    suspend fun getUnsyncedIncidents(): List<IncidentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity)

    @Query("UPDATE incidents SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Delete
    suspend fun deleteIncident(incident: IncidentEntity)

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getIncidentById(id: String): IncidentEntity?

    @Query("DELETE FROM incidents")
    suspend fun deleteAll()
}
