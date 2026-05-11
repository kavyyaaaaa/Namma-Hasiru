package com.nammahasiru.app.data.repository

import com.nammahasiru.app.data.database.PlantDao
import com.nammahasiru.app.data.database.PlantEntity
import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {

    fun getAllPlants(): Flow<List<PlantEntity>> = plantDao.getAllPlants()

    fun getPlantById(plantId: Int): Flow<PlantEntity?> = plantDao.getPlantById(plantId)

    suspend fun getPlantByIdOnce(plantId: Int): PlantEntity? = plantDao.getPlantByIdOnce(plantId)

    suspend fun insert(plant: PlantEntity): Long = plantDao.insert(plant)

    suspend fun update(plant: PlantEntity) = plantDao.update(plant)

    suspend fun delete(plant: PlantEntity) = plantDao.delete(plant)

    fun getTotalPlantCount(): Flow<Int> = plantDao.getTotalPlantCount()

    fun getAllSpeciesNames(): Flow<List<String>> = plantDao.getAllSpeciesNames()

    fun getPlantsBySpecies(species: String): Flow<List<PlantEntity>> =
        plantDao.getPlantsBySpecies(species)

    fun getPlantCountBySpecies(species: String): Flow<Int> =
        plantDao.getPlantCountBySpecies(species)

    /** Returns plants whose latest status matches [status] ("Alive" or "Dead"). */
    fun getPlantsByLatestStatus(status: String): Flow<List<PlantEntity>> =
        plantDao.getPlantsByLatestStatus(status)

    /** Returns plants that have no status updates yet (pending inspection). */
    fun getPendingPlants(): Flow<List<PlantEntity>> = plantDao.getPendingPlants()

    /** One-shot count for the dummy data seeder. */
    suspend fun getTotalPlantCountOnce(): Int = plantDao.getTotalPlantCountOnce()
}
