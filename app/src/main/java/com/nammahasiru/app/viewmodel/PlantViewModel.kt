package com.nammahasiru.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.nammahasiru.app.data.DummyDataSeeder
import com.nammahasiru.app.data.database.AppDatabase
import com.nammahasiru.app.data.database.PlantEntity
import com.nammahasiru.app.data.repository.PlantRepository
import com.nammahasiru.app.worker.PlantReminderWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PlantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlantRepository
    val allPlants: Flow<List<PlantEntity>>
    /** Plants whose most-recent status is Alive. */
    val alivePlants: Flow<List<PlantEntity>>
    /** Plants whose most-recent status is Dead. */
    val deadPlants: Flow<List<PlantEntity>>
    /** Plants that have no status updates yet (pending inspection). */
    val pendingPlants: Flow<List<PlantEntity>>

    private val _savingState = MutableStateFlow(false)
    val savingState: StateFlow<Boolean> = _savingState.asStateFlow()

    init {
        val plantDao = AppDatabase.getDatabase(application).plantDao()
        repository = PlantRepository(plantDao)
        allPlants    = repository.getAllPlants()
        alivePlants   = repository.getPlantsByLatestStatus("Alive")
        deadPlants    = repository.getPlantsByLatestStatus("Dead")
        pendingPlants = repository.getPendingPlants()

        // Populate demo data on first launch
        DummyDataSeeder.seedIfEmpty(application)
    }

    fun getPlantById(plantId: Int): Flow<PlantEntity?> = repository.getPlantById(plantId)

    fun addPlant(
        speciesName: String,
        photoPath: String,
        latitude: Double,
        longitude: Double,
        gpsAccuracy: Float?,
        datePlanted: Long,
        notes: String?
    ) {
        viewModelScope.launch {
            _savingState.value = true
            val plant = PlantEntity(
                speciesName = speciesName,
                photoPath = photoPath,
                latitude = latitude,
                longitude = longitude,
                gpsAccuracyM = gpsAccuracy,
                datePlanted = datePlanted,
                notes = notes
            )
            val plantId = repository.insert(plant)
            scheduleReminder(plantId.toInt(), speciesName, datePlanted)
            _savingState.value = false
        }
    }

    fun updatePlant(plant: PlantEntity) {
        viewModelScope.launch {
            repository.update(plant)
        }
    }

    fun deletePlant(plant: PlantEntity) {
        viewModelScope.launch {
            // Cancel the reminder if exists
            plant.reminderJobId?.let { jobId ->
                WorkManager.getInstance(getApplication()).cancelWorkById(
                    java.util.UUID.fromString(jobId)
                )
            }
            repository.delete(plant)
        }
    }

    private fun scheduleReminder(plantId: Int, speciesName: String, datePlanted: Long) {
        val inputData = Data.Builder()
            .putInt("plant_id", plantId)
            .putString("species_name", speciesName)
            .putLong("date_planted", datePlanted)
            .build()

        val reminderRequest = OneTimeWorkRequestBuilder<PlantReminderWorker>()
            .setInitialDelay(90, TimeUnit.DAYS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(getApplication()).enqueue(reminderRequest)

        // Store the job ID in the plant record
        viewModelScope.launch {
            val plant = repository.getPlantByIdOnce(plantId)
            plant?.let {
                repository.update(it.copy(reminderJobId = reminderRequest.id.toString()))
            }
        }
    }
}
