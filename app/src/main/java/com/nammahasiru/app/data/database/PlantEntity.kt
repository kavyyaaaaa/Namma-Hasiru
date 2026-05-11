package com.nammahasiru.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val speciesName: String,
    val photoPath: String,
    val latitude: Double,
    val longitude: Double,
    val gpsAccuracyM: Float? = null,
    val datePlanted: Long,
    val notes: String? = null,
    val reminderJobId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
