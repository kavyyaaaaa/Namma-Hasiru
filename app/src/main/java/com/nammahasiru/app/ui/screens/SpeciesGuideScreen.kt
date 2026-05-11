package com.nammahasiru.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nammahasiru.app.ui.theme.AccentLeaf
import com.nammahasiru.app.ui.theme.AlertRed
import com.nammahasiru.app.ui.theme.AmberPending
import com.nammahasiru.app.ui.theme.Background
import com.nammahasiru.app.ui.theme.EarthBrown
import com.nammahasiru.app.ui.theme.PrimaryGreen
import com.nammahasiru.app.ui.theme.SurfaceWhite
import com.nammahasiru.app.viewmodel.SpeciesDetail
import com.nammahasiru.app.viewmodel.SpeciesViewModel

private val categoryColors = mapOf(
    "Fruit"     to AccentLeaf,
    "Shade"     to PrimaryGreen,
    "Medicinal" to Color(0xFF00796B),
    "Biofuel"   to EarthBrown
)

private val soilColors = mapOf(
    "Red Soil"   to Color(0xFFBF360C),
    "Black Soil" to Color(0xFF37474F),
    "Sandy Soil" to Color(0xFFF9A825),
    "Clay Soil"  to Color(0xFF6D4C41)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesGuideScreen(
    onNavigateBack: () -> Unit,
    speciesViewModel: SpeciesViewModel = viewModel()
) {
    val allDetails = speciesViewModel.staticSpeciesDetails

    // Active category filter; null = show all
    var activeCategory by remember { mutableStateOf<String?>(null) }

    val categories = listOf("All", "Fruit", "Shade", "Medicinal", "Biofuel")

    val filtered = if (activeCategory == null || activeCategory == "All")
        allDetails
    else
        allDetails.filter { it.category == activeCategory }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Species Guide", fontWeight = FontWeight.Bold)
                        Text(
                            "Tap a card to expand farming details",
                            fontSize = 11.sp,
                            color = SurfaceWhite.copy(alpha = 0.8f)
                        )
                    }
                },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Category filter chips ─────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val selected = (cat == "All" && activeCategory == null) ||
                                cat == activeCategory
                        FilterChip(
                            selected = selected,
                            onClick  = { activeCategory = if (cat == "All") null else cat },
                            label    = { Text(cat, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor     = SurfaceWhite
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // ── Header text ───────────────────────────────────────────────
            item {
                Text(
                    "${filtered.size} species found",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // ── Species expandable cards ──────────────────────────────────
            items(filtered) { detail ->
                SpeciesDetailCard(detail = detail)
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Expandable species card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SpeciesDetailCard(detail: SpeciesDetail) {
    var expanded by remember { mutableStateOf(false) }

    val healthColor = when {
        detail.healthPercent >= 60 -> AccentLeaf
        detail.healthPercent >= 40 -> AmberPending
        else                        -> AlertRed
    }

    val animatedHealth by animateFloatAsState(
        targetValue   = detail.healthPercent / 100f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label         = "health_${detail.name}"
    )

    val categoryColor = categoryColors[detail.category] ?: PrimaryGreen
    val soilColor     = soilColors[detail.soilType] ?: EarthBrown

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header row ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji badge
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(detail.emoji, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        detail.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Category chip
                        MiniChip(label = detail.category, color = categoryColor)
                        // Soil chip
                        MiniChip(label = detail.soilType, color = soilColor)
                    }
                }

                // Health % + expand icon
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${detail.healthPercent}%",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = healthColor
                    )
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ── Health progress bar ───────────────────────────────────────
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Health", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(
                    if (detail.healthPercent >= 60) "Healthy"
                    else if (detail.healthPercent >= 40) "Moderate"
                    else "Critical",
                    style = MaterialTheme.typography.bodySmall,
                    color = healthColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress   = { animatedHealth },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color      = healthColor,
                trackColor = Color.LightGray.copy(alpha = 0.4f)
            )

            // ── Collapsed quick-info row ──────────────────────────────────
            if (!expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    QuickInfoBadge(Icons.Default.WaterDrop, detail.waterNeeds, AccentLeaf)
                    QuickInfoBadge(Icons.Default.LightMode, detail.sunlight, AmberPending)
                    QuickInfoBadge(Icons.Default.Schedule, detail.growthDuration, PrimaryGreen)
                }
            }

            // ── Expanded farming detail ───────────────────────────────────
            AnimatedVisibility(
                visible = expanded,
                enter   = expandVertically(animationSpec = tween(300)),
                exit    = shrinkVertically(animationSpec = tween(300))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    // Description
                    Text(
                        detail.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2-column detail grid
                    val rows = listOf(
                        Triple(Icons.Default.WaterDrop,  "Water",   detail.waterNeeds),
                        Triple(Icons.Default.LightMode,  "Sunlight",detail.sunlight),
                        Triple(Icons.Default.Grass,      "Season",  detail.bestSeason),
                        Triple(Icons.Default.Schedule,   "Growth",  detail.growthDuration),
                        Triple(Icons.Default.ShoppingBag,"Harvest", detail.harvestInfo),
                        Triple(Icons.Default.Park,       "Soil",    detail.soilType)
                    )

                    rows.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { (icon, label, value) ->
                                DetailInfoCell(
                                    modifier = Modifier.weight(1f),
                                    icon     = icon,
                                    label    = label,
                                    value    = value
                                )
                            }
                            // If odd item, fill remaining space
                            if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helper composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MiniChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun QuickInfoBadge(icon: ImageVector, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
    }
}

@Composable
private fun DetailInfoCell(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(10.dp),
        colors   = CardDefaults.cardColors(containerColor = Background)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(16.dp).padding(top = 1.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                Text(value, fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
            }
        }
    }
}
