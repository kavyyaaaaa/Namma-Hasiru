package com.nammahasiru.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.nammahasiru.app.data.database.StatusEntity
import com.nammahasiru.app.ui.theme.AccentLeaf
import com.nammahasiru.app.ui.theme.AlertRed
import com.nammahasiru.app.ui.theme.AmberPending
import com.nammahasiru.app.ui.theme.PrimaryGreen
import com.nammahasiru.app.ui.theme.SurfaceWhite
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun GrowthTimelineItem(
    status: StatusEntity,
    dateFormat: SimpleDateFormat,
    isLast: Boolean,
    isFirst: Boolean
) {
    val statusColor = when (status.statusValue) {
        "Alive" -> AccentLeaf
        "Dead" -> AlertRed
        else -> AmberPending
    }

    val percentColor = when {
        status.growthPercent >= 100 -> PrimaryGreen
        status.growthPercent >= 50 -> AccentLeaf
        status.growthPercent >= 25 -> AmberPending
        else -> Color(0xFF78909C)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Timeline rail (line + node)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Top connector line
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(16.dp)
                        .background(PrimaryGreen.copy(alpha = 0.4f))
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Node circle
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(percentColor),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                )
            }

            // Bottom connector line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .weight(1f)
                        .fillMaxHeight()
                        .background(PrimaryGreen.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header row: percentage badge + date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Percentage badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(percentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "${status.growthPercent}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = percentColor
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            status.statusValue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Date
                    Text(
                        dateFormat.format(Date(status.updateDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Growth photo
                status.growthPhotoPath?.let { path ->
                    val photoFile = File(path)
                    if (photoFile.exists()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Image(
                            painter = rememberAsyncImagePainter(photoFile),
                            contentDescription = "Growth Photo at ${status.growthPercent}%",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Description / observation notes
                status.observationNotes?.let { notes ->
                    if (notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF424242)
                        )
                    }
                }

                // Height info
                status.heightCm?.let { height ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Height: ${height}cm",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
