package com.nammahasiru.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "status_updates",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["plantId"])]
)
data class StatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plantId: Int,
    val statusValue: String, // "Alive", "Dead", "Unknown"
    val growthPercent: Int = 0,
    val growthPhotoPath: String? = null,
    val heightCm: Int? = null,
    val observationNotes: String? = null,
    val updateDate: Long = System.currentTimeMillis()
)
