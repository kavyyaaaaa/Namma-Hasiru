package com.nammahasiru.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nammahasiru.app.data.database.AppDatabase
import com.nammahasiru.app.data.repository.PlantRepository
import com.nammahasiru.app.data.repository.StatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

// ── Existing SpeciesInfo (kept for backward compatibility) ────────────────────
data class SpeciesInfo(
    val speciesName: String,
    val totalPlanted: Int,
    val aliveCount: Int,
    val survivalRate: Float
)

// ── Rich static species detail shown in the redesigned Species Guide ──────────
data class SpeciesDetail(
    val name: String,
    val emoji: String,
    val soilType: String,
    val waterNeeds: String,      // "Low" | "Medium" | "High"
    val sunlight: String,
    val bestSeason: String,
    val growthDuration: String,
    val harvestInfo: String,
    val description: String,
    val category: String,        // "Fruit" | "Shade" | "Medicinal" | "Biofuel"
    /** Seeded value 30-80 so each species always shows a consistent health bar. */
    val healthPercent: Int
)

class SpeciesViewModel(application: Application) : AndroidViewModel(application) {

    private val plantRepository: PlantRepository
    private val statusRepository: StatusRepository

    /** Species names that actually exist in the DB (for DB-backed section). */
    val allSpecies: Flow<List<String>>

    init {
        val db = AppDatabase.getDatabase(application)
        plantRepository = PlantRepository(db.plantDao())
        statusRepository = StatusRepository(db.statusDao())
        allSpecies = plantRepository.getAllSpeciesNames()
    }

    fun getSpeciesInfo(speciesName: String): Flow<SpeciesInfo> {
        val totalFlow = plantRepository.getPlantCountBySpecies(speciesName)
        val aliveFlow = statusRepository.getAliveCountBySpecies(speciesName)

        return combine(totalFlow, aliveFlow) { total, alive ->
            val rate = if (total > 0) (alive.toFloat() / total.toFloat()) * 100f else 0f
            SpeciesInfo(
                speciesName  = speciesName,
                totalPlanted = total,
                aliveCount   = alive,
                survivalRate = rate
            )
        }
    }

    // ── Static encyclopaedia data (always visible, even on fresh installs) ──
    val staticSpeciesDetails: List<SpeciesDetail> = listOf(
        SpeciesDetail(
            name          = "Neem",
            emoji         = "🌿",
            soilType      = "Red Soil",
            waterNeeds    = "Medium",
            sunlight      = "Full Sun",
            bestSeason    = "Monsoon (Jun – Sep)",
            growthDuration = "3 – 5 years",
            harvestInfo   = "Medicinal leaves, seeds & bark year-round",
            description   = "Highly drought-tolerant tree with powerful antibacterial " +
                            "properties. Grows well in dry, semi-arid conditions.",
            category      = "Medicinal",
            healthPercent = 72
        ),
        SpeciesDetail(
            name          = "Mango",
            emoji         = "🥭",
            soilType      = "Black Soil",
            waterNeeds    = "High",
            sunlight      = "Full Sun",
            bestSeason    = "Pre-Monsoon (Mar – May)",
            growthDuration = "3 – 4 years",
            harvestInfo   = "Seasonal fruit harvest; 100 – 200 fruits/tree/year",
            description   = "India's national fruit. Thrives in deep, well-drained " +
                            "black cotton soil with ample summer irrigation.",
            category      = "Fruit",
            healthPercent = 65
        ),
        SpeciesDetail(
            name          = "Coconut",
            emoji         = "🥥",
            soilType      = "Sandy Soil",
            waterNeeds    = "High",
            sunlight      = "Full Sun",
            bestSeason    = "Year-round (coastal zones)",
            growthDuration = "6 – 10 years",
            harvestInfo   = "Copra, coconut oil, coir fibre; 50 – 200 nuts/year",
            description   = "Thrives near coastlines in well-drained sandy loam. " +
                            "Tolerates saline air but needs high moisture.",
            category      = "Fruit",
            healthPercent = 58
        ),
        SpeciesDetail(
            name          = "Banyan",
            emoji         = "🌳",
            soilType      = "Clay Soil",
            waterNeeds    = "Medium",
            sunlight      = "Full Sun",
            bestSeason    = "Monsoon (Jun – Sep)",
            growthDuration = "5 – 10 years",
            harvestInfo   = "No commercial harvest; invaluable shade & ecosystem",
            description   = "India's national tree. Aerial roots form large groves. " +
                            "Ideal for village squares and sacred groves.",
            category      = "Shade",
            healthPercent = 45
        ),
        SpeciesDetail(
            name          = "Peepal",
            emoji         = "🍃",
            soilType      = "Red Soil",
            waterNeeds    = "Low",
            sunlight      = "Full Sun",
            bestSeason    = "Monsoon (Jun – Sep)",
            growthDuration = "3 – 5 years",
            harvestInfo   = "Medicinal bark & leaves; maximum O₂ at night",
            description   = "One of the longest-lived trees; produces oxygen 24 hours. " +
                            "Sacred in Buddhism, Hinduism and Jainism.",
            category      = "Medicinal",
            healthPercent = 38
        ),
        SpeciesDetail(
            name          = "Pongamia pinnata",
            emoji         = "🌱",
            soilType      = "Black Soil",
            waterNeeds    = "Low",
            sunlight      = "Full Sun",
            bestSeason    = "Monsoon (Jun – Sep)",
            growthDuration = "4 – 7 years",
            harvestInfo   = "Seed oil for biofuel/biodiesel; 9 – 90 kg seed/tree/year",
            description   = "Nitrogen-fixing legume tree; ideal for agroforestry, " +
                            "wastelands and roadside plantation. Drought hardy.",
            category      = "Biofuel",
            healthPercent = 52
        )
    )
}
