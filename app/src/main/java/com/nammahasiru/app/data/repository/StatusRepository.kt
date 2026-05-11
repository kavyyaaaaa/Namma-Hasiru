package com.nammahasiru.app.data.repository

import com.nammahasiru.app.data.database.StatusDao
import com.nammahasiru.app.data.database.StatusEntity
import kotlinx.coroutines.flow.Flow

class StatusRepository(private val statusDao: StatusDao) {

    suspend fun insert(status: StatusEntity): Long = statusDao.insert(status)

    fun getStatusUpdatesForPlant(plantId: Int): Flow<List<StatusEntity>> =
        statusDao.getStatusUpdatesForPlant(plantId)

    fun getLatestStatusForPlant(plantId: Int): Flow<StatusEntity?> =
        statusDao.getLatestStatusForPlant(plantId)

    suspend fun getLatestStatusForPlantOnce(plantId: Int): StatusEntity? =
        statusDao.getLatestStatusForPlantOnce(plantId)

    fun getAliveCount(): Flow<Int> = statusDao.getAliveCount()

    fun getDeadCount(): Flow<Int> = statusDao.getDeadCount()

    fun getPlantsWithStatusCount(): Flow<Int> = statusDao.getPlantsWithStatusCount()

    fun getAliveCountBySpecies(species: String): Flow<Int> =
        statusDao.getAliveCountBySpecies(species)

    fun getPlantsWithStatusBySpecies(species: String): Flow<Int> =
        statusDao.getPlantsWithStatusBySpecies(species)

    fun getMaxGrowthPercent(plantId: Int): Flow<Int> =
        statusDao.getMaxGrowthPercent(plantId)

    fun getStatusUpdatesForPlantAsc(plantId: Int): Flow<List<StatusEntity>> =
        statusDao.getStatusUpdatesForPlantAsc(plantId)
}
