package com.nammahasiru.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nammahasiru.app.data.database.PlantEntity
import com.nammahasiru.app.ui.theme.AccentLeaf
import com.nammahasiru.app.ui.theme.AlertRed
import com.nammahasiru.app.ui.theme.AmberPending
import com.nammahasiru.app.ui.theme.Background
import com.nammahasiru.app.ui.theme.PrimaryGreen
import com.nammahasiru.app.ui.theme.SurfaceWhite
import com.nammahasiru.app.viewmodel.PlantViewModel
import com.nammahasiru.app.viewmodel.StatusViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A single screen that shows a filtered list of plants:
 *  - filterType = "total"   → all plants
 *  - filterType = "alive"   → plants whose latest status is Alive
 *  - filterType = "dead"    → plants whose latest status is Dead
 *  - filterType = "pending" → plants with no status update yet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilteredPlantListScreen(
    filterType: String,
    onNavigateBack: () -> Unit,
    onNavigateToPlantDetail: (Int) -> Unit,
    plantViewModel: PlantViewModel = viewModel(),
    statusViewModel: StatusViewModel = viewModel()
) {
    val allPlants     by plantViewModel.allPlants.collectAsState(initial = emptyList())
    val alivePlants   by plantViewModel.alivePlants.collectAsState(initial = emptyList())
    val deadPlants    by plantViewModel.deadPlants.collectAsState(initial = emptyList())
    val pendingPlants by plantViewModel.pendingPlants.collectAsState(initial = emptyList())

    val (title, icon, accentColor, plants) = when (filterType) {
        "alive"   -> FilterConfig("Alive Plants",   Icons.Default.CheckCircle, AccentLeaf,    alivePlants)
        "dead"    -> FilterConfig("Dead Plants",    Icons.Default.Close,       AlertRed,      deadPlants)
        "pending" -> FilterConfig("Pending Plants", Icons.Default.Schedule,    AmberPending,  pendingPlants)
        else      -> FilterConfig("All Plants",     Icons.Default.Park,        PrimaryGreen,  allPlants)
    }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(title, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = accentColor,
                    titleContentColor = SurfaceWhite,
                    navigationIconContentColor = SurfaceWhite
                )
            )
        }
    ) { padding ->
        if (plants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No $title yet", color = Color.Gray, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Add or update plants to see them here.", color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${plants.size} plant${if (plants.size == 1) "" else "s"} found",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(plants) { plant ->
                    FilteredPlantItem(
                        plant         = plant,
                        filterType    = filterType,
                        accentColor   = accentColor,
                        icon          = icon,
                        dateFormat    = dateFormat,
                        statusViewModel = statusViewModel,
                        onClick       = { onNavigateToPlantDetail(plant.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Individual plant row card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FilteredPlantItem(
    plant: PlantEntity,
    filterType: String,
    accentColor: Color,
    icon: ImageVector,
    dateFormat: SimpleDateFormat,
    statusViewModel: StatusViewModel,
    onClick: () -> Unit
) {
    val latestStatus by statusViewModel.getLatestStatusForPlant(plant.id).collectAsState(initial = null)
    val growthPercent = latestStatus?.growthPercent ?: 0

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status icon badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(26.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        plant.speciesName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Planted: ${dateFormat.format(Date(plant.datePlanted))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        "GPS: %.4f°N, %.4f°E".format(plant.latitude, plant.longitude),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }

            // Growth % bar (only if not pending)
            if (filterType != "pending") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Growth",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        "$growthPercent%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress    = { growthPercent / 100f },
                    modifier    = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color       = accentColor,
                    trackColor  = Color.LightGray.copy(alpha = 0.4f)
                )
            }

            // Extra metadata row
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusChip(
                    label = when (filterType) {
                        "alive"   -> "Alive"
                        "dead"    -> "Dead"
                        "pending" -> "Pending Check-up"
                        else      -> latestStatus?.statusValue ?: "Pending"
                    },
                    color = accentColor
                )
                latestStatus?.observationNotes?.let { reason ->
                    if (reason.isNotBlank()) {
                        Text(
                            reason,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Internal config holder to avoid 4 separate parameters
// ─────────────────────────────────────────────────────────────────────────────
private data class FilterConfig(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val plants: List<PlantEntity>
)
