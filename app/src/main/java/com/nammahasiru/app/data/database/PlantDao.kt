package com.nammahasiru.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: PlantEntity): Long

    @Update
    suspend fun update(plant: PlantEntity)

    @Delete
    suspend fun delete(plant: PlantEntity)

    @Query("SELECT * FROM plants ORDER BY createdAt DESC")
    fun getAllPlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    fun getPlantById(plantId: Int): Flow<PlantEntity?>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantByIdOnce(plantId: Int): PlantEntity?

    @Query("SELECT COUNT(*) FROM plants")
    fun getTotalPlantCount(): Flow<Int>

    /** One-shot count used by [DummyDataSeeder] to check if DB is empty. */
    @Query("SELECT COUNT(*) FROM plants")
    suspend fun getTotalPlantCountOnce(): Int

    @Query("SELECT DISTINCT speciesName FROM plants")
    fun getAllSpeciesNames(): Flow<List<String>>

    @Query("SELECT * FROM plants WHERE speciesName = :species")
    fun getPlantsBySpecies(species: String): Flow<List<PlantEntity>>

    @Query("SELECT COUNT(*) FROM plants WHERE speciesName = :species")
    fun getPlantCountBySpecies(species: String): Flow<Int>

    /**
     * Returns all plants whose most-recent status entry matches [status]
     * (e.g. "Alive" or "Dead").
     */
    @Query("""
        SELECT DISTINCT p.* FROM plants p
        INNER JOIN status_updates s ON p.id = s.plantId
        WHERE s.id = (
            SELECT s2.id FROM status_updates s2
            WHERE s2.plantId = p.id
            ORDER BY s2.updateDate DESC LIMIT 1
        ) AND s.statusValue = :status
        ORDER BY p.createdAt DESC
    """)
    fun getPlantsByLatestStatus(status: String): Flow<List<PlantEntity>>

    /** Returns plants that have NO status updates yet (pending inspection). */
    @Query("""
        SELECT * FROM plants WHERE id NOT IN (
            SELECT DISTINCT plantId FROM status_updates
        ) ORDER BY createdAt DESC
    """)
    fun getPendingPlants(): Flow<List<PlantEntity>>
}
