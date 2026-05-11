package com.nammahasiru.app.ui.screens

import android.Manifest
import android.content.Context
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.nammahasiru.app.ui.theme.AlertRed
import com.nammahasiru.app.ui.theme.PrimaryGreen
import com.nammahasiru.app.ui.theme.SurfaceWhite
import com.nammahasiru.app.viewmodel.PlantViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    onNavigateBack: () -> Unit,
    plantViewModel: PlantViewModel = viewModel()
) {
    val context = LocalContext.current
    val savingState by plantViewModel.savingState.collectAsState()

    var speciesName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var photoPath by remember { mutableStateOf<String?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var gpsAccuracy by remember { mutableFloatStateOf(0f) }
    var locationTagged by remember { mutableStateOf(false) }
    var datePlanted by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Validation errors
    var speciesError by remember { mutableStateOf<String?>(null) }
    var photoError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

    // Camera launcher
    val tempFile = remember { createImageFile(context) }
    val tempUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoPath = tempFile.absolutePath
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
            Toast.makeText(context, "Camera permission needed to capture plant photo.", Toast.LENGTH_LONG).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fetchLocation(context) { lat, lon, acc ->
                latitude = lat
                longitude = lon
                gpsAccuracy = acc
                locationTagged = true
                locationError = null
            }
        } else {
            Toast.makeText(context, "Location permission needed.", Toast.LENGTH_LONG).show()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = datePlanted)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selected ->
                        if (selected <= System.currentTimeMillis()) {
                            datePlanted = selected
                        } else {
                            Toast.makeText(context, "Date cannot be in the future.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Plant") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Section
            Text("Plant Photo *", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
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
                        contentDescription = "Plant Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Take Photo",
                            modifier = Modifier.size(48.dp),
                            tint = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap to take photo", color = Color.Gray)
                    }
                }
            }
            photoError?.let {
                Text(it, color = AlertRed, style = MaterialTheme.typography.bodySmall)
            }

            // GPS Location
            Text("GPS Location *", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedButton(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = if (locationError != null)
                    androidx.compose.foundation.BorderStroke(2.dp, AlertRed)
                else
                    ButtonDefaults.outlinedButtonBorder
            ) {
                Icon(
                    if (locationTagged) Icons.Default.LocationOn else Icons.Default.MyLocation,
                    contentDescription = "Tag Location",
                    tint = if (locationTagged) PrimaryGreen else Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (locationTagged)
                        "Lat: ${"%.5f".format(latitude)}, Lon: ${"%.5f".format(longitude)}"
                    else
                        "Tag GPS Location",
                    color = if (locationTagged) PrimaryGreen else Color.Gray
                )
            }
            locationError?.let {
                Text(it, color = AlertRed, style = MaterialTheme.typography.bodySmall)
            }

            // Species Name
            Text("Species Name *", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = speciesName,
                onValueChange = {
                    if (it.length <= 80) {
                        speciesName = it
                        speciesError = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., Pongamia pinnata") },
                isError = speciesError != null,
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        speciesError?.let { Text(it, color = AlertRed) }
                            ?: Text("")
                        Text("${speciesName.length}/80")
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Date Planted
            Text("Date Planted *", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick Date", tint = PrimaryGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(datePlanted)),
                    color = PrimaryGreen
                )
            }

            // Notes
            Text("Notes (optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = notes,
                onValueChange = { if (it.length <= 150) notes = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Any observations...") },
                minLines = 3,
                maxLines = 5,
                supportingText = { Text("${notes.length}/150") },
                shape = RoundedCornerShape(12.dp)
            )

            // Save Button
            Button(
                onClick = {
                    // Validate
                    var valid = true
                    if (photoPath == null) {
                        photoError = "Photo is required."
                        valid = false
                    }
                    if (!locationTagged) {
                        locationError = "Please tag GPS location."
                        valid = false
                    }
                    if (speciesName.isBlank()) {
                        speciesError = "Species name is required."
                        valid = false
                    }
                    if (datePlanted > System.currentTimeMillis()) {
                        Toast.makeText(context, "Date cannot be in the future.", Toast.LENGTH_SHORT).show()
                        valid = false
                    }

                    if (valid) {
                        plantViewModel.addPlant(
                            speciesName = speciesName.trim(),
                            photoPath = photoPath!!,
                            latitude = latitude,
                            longitude = longitude,
                            gpsAccuracy = gpsAccuracy,
                            datePlanted = datePlanted,
                            notes = notes.ifBlank { null }
                        )
                        Toast.makeText(context, "Plant saved successfully!", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !savingState,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Save Plant", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageDir = File(context.filesDir, "plant_photos")
    if (!imageDir.exists()) imageDir.mkdirs()
    return File(imageDir, "PLANT_${timeStamp}.jpg")
}

@Suppress("MissingPermission")
private fun fetchLocation(
    context: Context,
    onResult: (Double, Double, Float) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cancellationTokenSource = CancellationTokenSource()

    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        cancellationTokenSource.token
    ).addOnSuccessListener { location ->
        if (location != null) {
            onResult(location.latitude, location.longitude, location.accuracy)
        } else {
            Toast.makeText(context, "GPS signal not found. Please move outdoors and retry.", Toast.LENGTH_LONG).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Failed to get location. Please try again.", Toast.LENGTH_LONG).show()
    }
}
