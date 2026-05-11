package com.nammahasiru.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nammahasiru.app.data.database.AppDatabase
import com.nammahasiru.app.data.repository.PlantRepository
import com.nammahasiru.app.data.repository.StatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository
    private val statusRepository: StatusRepository

    val totalPlanted: Flow<Int>
    val aliveCount: Flow<Int>
    val deadCount: Flow<Int>
    val plantsWithStatus: Flow<Int>

    init {
        val db = AppDatabase.getDatabase(application)
        plantRepository = PlantRepository(db.plantDao())
        statusRepository = StatusRepository(db.statusDao())

        totalPlanted = plantRepository.getTotalPlantCount()
        aliveCount = statusRepository.getAliveCount()
        deadCount = statusRepository.getDeadCount()
        plantsWithStatus = statusRepository.getPlantsWithStatusCount()
    }

    // Survival score: (alive / total-with-status) × 100; defaults to 80 % when DB is empty
    val survivalScore: Flow<Float> = combine(aliveCount, plantsWithStatus) { alive, total ->
        if (total > 0) (alive.toFloat() / total.toFloat()) * 100f else 80f
    }

    // Pending check-up: total plants minus plants that have at least one status
    val pendingCheckup: Flow<Int> = combine(totalPlanted, plantsWithStatus) { total, withStatus ->
        (total - withStatus).coerceAtLeast(0)
    }
}
