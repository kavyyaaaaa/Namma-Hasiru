package com.nammahasiru.app.data

import android.content.Context
import com.nammahasiru.app.data.database.AppDatabase
import com.nammahasiru.app.data.database.PlantEntity
import com.nammahasiru.app.data.database.StatusEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * One-time seeder that inserts demo plantation records if the database is empty.
 * All GPS coordinates are real villages / peri-urban zones around Bengaluru.
 */
object DummyDataSeeder {

    // (speciesName, latitude, longitude, soilType)
    private data class DemoPlant(
        val species: String,
        val lat: Double,
        val lng: Double,
        val notes: String
    )

    // (statusValue, growthPercent, observationNotes)
    private data class DemoStatus(
        val status: String,
        val growth: Int,
        val notes: String?
    )

    private val plants = listOf(
        DemoPlant("Neem",              12.9716, 77.5946, "Red Soil – drought tolerant"),
        DemoPlant("Mango",             12.9650, 77.6100, "Black Soil – near water source"),
        DemoPlant("Coconut",           13.0100, 77.5500, "Sandy Soil – coastal strip"),
        DemoPlant("Banyan",            12.9800, 77.6200, "Clay Soil – village square"),
        DemoPlant("Peepal",            12.9580, 77.5860, "Red Soil – roadside plantation"),
        DemoPlant("Pongamia pinnata",  12.9900, 77.6050, "Black Soil – biofuel plot")
    )

    /** Alive=3, Dead=1 (Banyan – no water), Pending=2 (Peepal & Pongamia) */
    private val statuses = listOf(
        DemoStatus("Alive", 45, "Healthy, regular watering"),       // Neem
        DemoStatus("Alive", 62, "Good canopy growth"),              // Mango
        DemoStatus("Alive", 78, "Thriving, new fronds visible"),    // Coconut
        DemoStatus("Dead",  10, "No water – soil dried out"),       // Banyan
        null,                                                        // Peepal – pending
        null                                                         // Pongamia – pending
    )

    /**
     * Call this once from the Application or ViewModel init.
     * Uses its own IO coroutine so it never blocks the caller.
     */
    fun seedIfEmpty(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db  = AppDatabase.getDatabase(context)
            val plantDao  = db.plantDao()
            val statusDao = db.statusDao()

            if (plantDao.getTotalPlantCountOnce() > 0) return@launch  // already seeded

            val now      = System.currentTimeMillis()
            val day      = 24 * 60 * 60 * 1000L

            plants.forEachIndexed { index, demo ->
                // Stagger dates: most recent first
                val datePlanted = now - ((index + 1) * 7 * day)

                val plant = PlantEntity(
                    speciesName  = demo.species,
                    photoPath    = "",
                    latitude     = demo.lat,
                    longitude    = demo.lng,
                    datePlanted  = datePlanted,
                    notes        = demo.notes,
                    createdAt    = datePlanted
                )
                val plantId = plantDao.insert(plant).toInt()

                statuses[index]?.let { s ->
                    statusDao.insert(
                        StatusEntity(
                            plantId          = plantId,
                            statusValue      = s.status,
                            growthPercent    = s.growth,
                            observationNotes = s.notes,
                            updateDate       = datePlanted + day // checked 1 day after planting
                        )
                    )
                }
            }
        }
    }
}
