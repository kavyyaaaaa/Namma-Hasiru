package com.nammahasiru.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nammahasiru.app.data.database.AppDatabase
import com.nammahasiru.app.data.database.StatusEntity
import com.nammahasiru.app.data.repository.StatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StatusRepository

    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting.asStateFlow()

    init {
        val statusDao = AppDatabase.getDatabase(application).statusDao()
        repository = StatusRepository(statusDao)
    }

    fun getStatusUpdatesForPlant(plantId: Int): Flow<List<StatusEntity>> =
        repository.getStatusUpdatesForPlant(plantId)

    fun getLatestStatusForPlant(plantId: Int): Flow<StatusEntity?> =
        repository.getLatestStatusForPlant(plantId)

    fun getMaxGrowthPercent(plantId: Int): Flow<Int> =
        repository.getMaxGrowthPercent(plantId)

    fun getStatusUpdatesForPlantAsc(plantId: Int): Flow<List<StatusEntity>> =
        repository.getStatusUpdatesForPlantAsc(plantId)

    fun submitStatusUpdate(
        plantId: Int,
        statusValue: String,
        growthPercent: Int = 0,
        growthPhotoPath: String? = null,
        heightCm: Int? = null,
        observationNotes: String? = null
    ) {
        viewModelScope.launch {
            _submitting.value = true
            val status = StatusEntity(
                plantId = plantId,
                statusValue = statusValue,
                growthPercent = growthPercent,
                growthPhotoPath = growthPhotoPath,
                heightCm = heightCm,
                observationNotes = observationNotes
            )
            repository.insert(status)
            _submitting.value = false
        }
    }
}
