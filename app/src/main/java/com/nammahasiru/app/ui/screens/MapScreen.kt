package com.nammahasiru.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.nammahasiru.app.data.database.PlantEntity
import com.nammahasiru.app.ui.theme.PrimaryGreen
import com.nammahasiru.app.ui.theme.SurfaceWhite
import com.nammahasiru.app.viewmodel.PlantViewModel
import com.nammahasiru.app.viewmodel.StatusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlantDetail: (Int) -> Unit,
    plantViewModel: PlantViewModel = viewModel(),
    statusViewModel: StatusViewModel = viewModel()
) {
    val plants by plantViewModel.allPlants.collectAsState(initial = emptyList())

    // Default camera centred on Bengaluru; shifts to first plant when data loads
    val defaultPosition = LatLng(12.9716, 77.5946)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            if (plants.isNotEmpty()) LatLng(plants.first().latitude, plants.first().longitude)
            else defaultPosition,
            12f
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tree Map") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = SurfaceWhite,
                    navigationIconContentColor = SurfaceWhite
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                plants.forEach { plant ->
                    PlantMarker(
                        plant           = plant,
                        statusViewModel = statusViewModel,
                        onClick         = { onNavigateToPlantDetail(plant.id) }
                    )
                }
            }
        }
    }
}

/**
 * A map marker whose colour reflects the plant's latest status:
 *   Green  → Alive
 *   Red    → Dead
 *   Orange → Pending (no status update yet)
 *
 * The info-window snippet shows species name, growth %, and date planted.
 */
@Composable
fun PlantMarker(
    plant: PlantEntity,
    statusViewModel: StatusViewModel,
    onClick: () -> Unit
) {
    val latestStatus  by statusViewModel.getLatestStatusForPlant(plant.id).collectAsState(initial = null)
    val growthPercent  = latestStatus?.growthPercent ?: 0

    val markerColor = when (latestStatus?.statusValue) {
        "Alive" -> BitmapDescriptorFactory.HUE_GREEN
        "Dead"  -> BitmapDescriptorFactory.HUE_RED
        else    -> BitmapDescriptorFactory.HUE_ORANGE  // Pending
    }

    val statusLabel = latestStatus?.statusValue ?: "Pending check-up"
    val snippet     = "$statusLabel · Growth: $growthPercent%"

    Marker(
        state            = MarkerState(position = LatLng(plant.latitude, plant.longitude)),
        title            = plant.speciesName,
        snippet          = snippet,
        icon             = BitmapDescriptorFactory.defaultMarker(markerColor),
        onInfoWindowClick = { onClick() },
        onClick          = {
            it.showInfoWindow()
            true
        }
    )
}
