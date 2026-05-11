package com.nammahasiru.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammahasiru.app.ui.theme.PrimaryGreen
import com.nammahasiru.app.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "splash_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        onSplashComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryGreen),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Park,
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .alpha(alphaAnim),
            tint = SurfaceWhite
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Namma Hasiru",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = SurfaceWhite,
            modifier = Modifier.alpha(alphaAnim)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Every Tree Counts.",
            fontSize = 16.sp,
            color = SurfaceWhite.copy(alpha = 0.8f),
            modifier = Modifier.alpha(alphaAnim)
        )
    }
}
