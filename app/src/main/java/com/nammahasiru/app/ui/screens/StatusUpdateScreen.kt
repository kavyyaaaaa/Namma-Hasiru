package com.nammahasiru.app.ui.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.nammahasiru.app.ui.theme.AccentLeaf
import com.nammahasiru.app.ui.theme.AlertRed
import com.nammahasiru.app.ui.theme.AmberPending
import com.nammahasiru.app.ui.theme.PrimaryGreen
import com.nammahasiru.app.ui.theme.SurfaceWhite
import com.nammahasiru.app.viewmodel.StatusViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusUpdateScreen(
    plantId: Int,
    onNavigateBack: () -> Unit,
    statusViewModel: StatusViewModel = viewModel()
) {
    val context = LocalContext.current
    val submitting by statusViewModel.submitting.collectAsState()
    val currentMaxPercent by statusViewModel.getMaxGrowthPercent(plantId).collectAsState(initial = 0)

    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var heightCm by remember { mutableStateOf("") }
    var observationNotes by remember { mutableStateOf("") }
    var growthPhotoPath by remember { mutableStateOf<String?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var statusError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var photoError by remember { mutableStateOf<String?>(null) }

    // Growth percentage slider state — starts from the current max
    var growthSliderValue by remember(currentMaxPercent) {
        mutableFloatStateOf(currentMaxPercent.toFloat())
    }

    // Camera for growth photo
    val tempFile = remember {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageDir = File(context.filesDir, "growth_photos")
        if (!imageDir.exists()) imageDir.mkdirs()
        File(imageDir, "GROWTH_${timeStamp}.jpg")
    }
    val tempUri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            growthPhotoPath = tempFile.absolutePath
            photoUri = tempUri
            photoError = null
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(tempUri)
        } else {
            Toast.makeText(context, "Camera permission needed.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Growth Status") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Growth Percentage Slider ──
            Text(
                "Growth Progress *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column {
                // Percentage display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryGreen.copy(alpha = 0.08f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${growthSliderValue.toInt()}%",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                        Text(
                            when {
                                growthSliderValue >= 100f -> "🎉 Fully Grown!"
                                growthSliderValue >= 75f -> "Almost there!"
                                growthSliderValue >= 50f -> "Growing strong"
                                growthSliderValue >= 25f -> "Making progress"
                                else -> "Just getting started"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = growthSliderValue,
                    onValueChange = { newValue ->
                        // Enforce forward-only progress
                        if (newValue >= currentMaxPercent) {
                            growthSliderValue = newValue
                        }
                    },
                    valueRange = 0f..100f,
                    steps = 19, // 5% increments
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryGreen,
                        activeTrackColor = PrimaryGreen,
                        inactiveTrackColor = PrimaryGreen.copy(alpha = 0.2f)
                    )
                )

                if (currentMaxPercent > 0) {
                    Text(
                        "Previous progress: ${currentMaxPercent}% (cannot go below)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // ── Growth Photo (required) ──
            Text(
                "Growth Photo *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (photoError != null) 2.dp else 1.dp,
                        color = if (photoError != null) AlertRed else Color.LightGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(Color(0xFFF5F5F5))
                    .clickable { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUri),
                        contentDescription = "Growth Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Add Growth Photo",
                            modifier = Modifier.size(36.dp),
                            tint = if (photoError != null) AlertRed else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tap to take a growth photo",
                            color = if (photoError != null) AlertRed else Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            photoError?.let {
                Text(it, color = AlertRed, style = MaterialTheme.typography.bodySmall)
            }

            // ── Description (required) ──
            Text(
                "Description *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = observationNotes,
                onValueChange = {
                    if (it.length <= 300) {
                        observationNotes = it
                        descriptionError = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Describe the plant's current condition...") },
                minLines = 3,
                maxLines = 5,
                isError = descriptionError != null,
                supportingText = {
                    if (descriptionError != null) {
                        Text(descriptionError!!, color = AlertRed)
                    } else {
                        Text("${observationNotes.length}/300")
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            // ── Plant Status ──
            Text(
                "Plant Status *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            val statuses = listOf("Alive" to AccentLeaf, "Dead" to AlertRed, "Unknown" to AmberPending)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                statuses.forEach { (status, color) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (selectedStatus == status) 2.dp else 1.dp,
                                color = if (selectedStatus == status) color else Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                if (selectedStatus == status) color.copy(alpha = 0.08f) else Color.Transparent
                            )
                            .clickable {
                                selectedStatus = status
                                statusError = null
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = {
                                selectedStatus = status
                                statusError = null
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(status, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        if (selectedStatus == status) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            statusError?.let {
                Text(it, color = AlertRed, style = MaterialTheme.typography.bodySmall)
            }

            // ── Height (optional) ──
            Text(
                "Height in cm (optional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = heightCm,
                onValueChange = {
                    heightCm = it.filter { c -> c.isDigit() }
                    heightError = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., 45") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = heightError != null,
                supportingText = heightError?.let { { Text(it, color = AlertRed) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // ── Submit Button ──
            Button(
                onClick = {
                    var valid = true
                    if (selectedStatus == null) {
                        statusError = "Please select a status."
                        valid = false
                    }
                    if (growthPhotoPath == null) {
                        photoError = "Please take a growth photo."
                        valid = false
                    }
                    if (observationNotes.isBlank()) {
                        descriptionError = "Please add a description."
                        valid = false
                    }
                    if (heightCm.isNotBlank()) {
                        val h = heightCm.toIntOrNull()
                        if (h == null || h <= 0) {
                            heightError = "Height must be > 0."
                            valid = false
                        }
                    }

                    if (valid) {
                        statusViewModel.submitStatusUpdate(
                            plantId = plantId,
                            statusValue = selectedStatus!!,
                            growthPercent = growthSliderValue.toInt(),
                            growthPhotoPath = growthPhotoPath,
                            heightCm = heightCm.toIntOrNull(),
                            observationNotes = observationNotes.ifBlank { null }
                        )
                        Toast.makeText(context, "Growth status updated!", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !submitting,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Submit Growth Update", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
