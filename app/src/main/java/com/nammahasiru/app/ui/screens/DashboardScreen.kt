package com.nammahasiru.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nammahasiru.app.ui.theme.AccentLeaf
import com.nammahasiru.app.ui.theme.AlertRed
import com.nammahasiru.app.ui.theme.AmberPending
import com.nammahasiru.app.ui.theme.Background
import com.nammahasiru.app.ui.theme.PrimaryGreen
import com.nammahasiru.app.ui.theme.SurfaceWhite
import com.nammahasiru.app.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddPlant: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToSpecies: () -> Unit,
    onNavigateToPlantDetail: (Int) -> Unit,
    onNavigateToTotal: () -> Unit,
    onNavigateToAlive: () -> Unit,
    onNavigateToDead: () -> Unit,
    onNavigateToPending: () -> Unit,
    onLogout: () -> Unit = {},
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val totalPlanted  by dashboardViewModel.totalPlanted.collectAsState(initial = 0)
    val aliveCount    by dashboardViewModel.aliveCount.collectAsState(initial = 0)
    val deadCount     by dashboardViewModel.deadCount.collectAsState(initial = 0)
    val pendingCheckup by dashboardViewModel.pendingCheckup.collectAsState(initial = 0)
    val survivalScore by dashboardViewModel.survivalScore.collectAsState(initial = 80f)

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Park,
                            contentDescription = null,
                            tint = SurfaceWhite,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Namma Hasiru",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                "Community Plantation Tracker",
                                fontSize = 11.sp,
                                color = SurfaceWhite.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = SurfaceWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = SurfaceWhite
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlant,
                containerColor = PrimaryGreen,
                contentColor = SurfaceWhite,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant")
            }
        },
        bottomBar = {
            NavigationBar(containerColor = SurfaceWhite) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Forest, contentDescription = "Dashboard") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMap,
                    icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                    label = { Text("Map") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSpecies,
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Species") },
                    label = { Text("Species") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Village Survival Score ─────────────────────────────────────
            item {
                SurvivalScoreCard(survivalScore = survivalScore)
            }

            // ── Stat Cards Row 1: Total & Alive ───────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier  = Modifier.weight(1f),
                        title     = "Total Planted",
                        value     = totalPlanted.toString(),
                        color     = PrimaryGreen,
                        icon      = Icons.Default.Park,
                        onClick   = onNavigateToTotal
                    )
                    StatCard(
                        modifier  = Modifier.weight(1f),
                        title     = "Alive",
                        value     = aliveCount.toString(),
                        color     = AccentLeaf,
                        icon      = Icons.Default.CheckCircle,
                        onClick   = onNavigateToAlive
                    )
                }
            }

            // ── Stat Cards Row 2: Dead & Pending ──────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier  = Modifier.weight(1f),
                        title     = "Dead",
                        value     = deadCount.toString(),
                        color     = AlertRed,
                        icon      = Icons.Default.Close,
                        onClick   = onNavigateToDead
                    )
                    StatCard(
                        modifier  = Modifier.weight(1f),
                        title     = "Pending",
                        value     = pendingCheckup.toString(),
                        color     = AmberPending,
                        icon      = Icons.Default.Schedule,
                        onClick   = onNavigateToPending
                    )
                }
            }

            // ── Quick Actions ─────────────────────────────────────────────
            item {
                Text(
                    "Quick Actions",
                    style     = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier  = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        title    = "Tree Map",
                        icon     = Icons.Default.Map,
                        onClick  = onNavigateToMap
                    )
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        title    = "Species Guide",
                        icon     = Icons.Default.MenuBook,
                        onClick  = onNavigateToSpecies
                    )
                }
            }

            // Bottom padding so FAB never covers last card
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }

    // ── Logout confirmation dialog ─────────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = AlertRed
                )
            },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text  = { Text("Are you sure you want to sign out of Namma Hasiru?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Sign Out", color = AlertRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = PrimaryGreen)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Village Survival Score Card with animated circular progress
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SurvivalScoreCard(survivalScore: Float) {
    // Animate from 0 → current score so the ring "fills in" on load
    val animatedScore by animateFloatAsState(
        targetValue  = survivalScore / 100f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label        = "survivalScore"
    )

    val scoreColor = when {
        survivalScore >= 70f -> AccentLeaf
        survivalScore >= 40f -> AmberPending
        else                  -> AlertRed
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment         = Alignment.CenterVertically,
            horizontalArrangement     = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Village Survival Score",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = PrimaryGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Community plantation health",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(scoreColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (survivalScore >= 70f) "Healthy"
                        else if (survivalScore >= 40f) "Moderate"
                        else "Critical",
                        style = MaterialTheme.typography.bodySmall,
                        color = scoreColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Circular progress ring
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress     = { animatedScore },
                    modifier     = Modifier.size(80.dp),
                    color        = scoreColor,
                    trackColor   = Color.LightGray.copy(alpha = 0.4f),
                    strokeWidth  = 8.dp,
                    strokeCap    = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${survivalScore.toInt()}%",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 18.sp,
                        color      = scoreColor
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Clickable stat card (Total / Alive / Dead / Pending)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier  = modifier.clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick-action card (Map / Species)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick   = onClick,
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f))
    ) {
        Column(
            modifier  = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = PrimaryGreen, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = PrimaryGreen, fontWeight = FontWeight.Bold)
        }
    }
}
