package com.nammahasiru.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(status: StatusEntity): Long

    @Query("SELECT * FROM status_updates WHERE plantId = :plantId ORDER BY updateDate DESC")
    fun getStatusUpdatesForPlant(plantId: Int): Flow<List<StatusEntity>>

    @Query("SELECT * FROM status_updates WHERE plantId = :plantId ORDER BY updateDate DESC LIMIT 1")
    fun getLatestStatusForPlant(plantId: Int): Flow<StatusEntity?>

    @Query("SELECT * FROM status_updates WHERE plantId = :plantId ORDER BY updateDate DESC LIMIT 1")
    suspend fun getLatestStatusForPlantOnce(plantId: Int): StatusEntity?

    @Query("DELETE FROM status_updates WHERE plantId = :plantId")
    suspend fun deleteAllForPlant(plantId: Int)

    @Query("SELECT COALESCE(MAX(growthPercent), 0) FROM status_updates WHERE plantId = :plantId")
    fun getMaxGrowthPercent(plantId: Int): Flow<Int>

    @Query("SELECT * FROM status_updates WHERE plantId = :plantId ORDER BY updateDate ASC")
    fun getStatusUpdatesForPlantAsc(plantId: Int): Flow<List<StatusEntity>>

    // Count of plants that have at least one status update with statusValue = 'Alive' as latest
    @Query("""
        SELECT COUNT(DISTINCT s.plantId) FROM status_updates s 
        WHERE s.id IN (
            SELECT s2.id FROM status_updates s2 
            WHERE s2.plantId = s.plantId 
            ORDER BY s2.updateDate DESC LIMIT 1
        ) AND s.statusValue = 'Alive'
    """)
    fun getAliveCount(): Flow<Int>

    @Query("""
        SELECT COUNT(DISTINCT s.plantId) FROM status_updates s 
        WHERE s.id IN (
            SELECT s2.id FROM status_updates s2 
            WHERE s2.plantId = s.plantId 
            ORDER BY s2.updateDate DESC LIMIT 1
        ) AND s.statusValue = 'Dead'
    """)
    fun getDeadCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT plantId) FROM status_updates")
    fun getPlantsWithStatusCount(): Flow<Int>

    // Survival rate by species
    @Query("""
        SELECT COUNT(DISTINCT s.plantId) FROM status_updates s 
        INNER JOIN plants p ON s.plantId = p.id
        WHERE p.speciesName = :species
        AND s.id IN (
            SELECT s2.id FROM status_updates s2 
            WHERE s2.plantId = s.plantId 
            ORDER BY s2.updateDate DESC LIMIT 1
        ) AND s.statusValue = 'Alive'
    """)
    fun getAliveCountBySpecies(species: String): Flow<Int>

    @Query("""
        SELECT COUNT(DISTINCT s.plantId) FROM status_updates s 
        INNER JOIN plants p ON s.plantId = p.id
        WHERE p.speciesName = :species
    """)
    fun getPlantsWithStatusBySpecies(species: String): Flow<Int>
}
