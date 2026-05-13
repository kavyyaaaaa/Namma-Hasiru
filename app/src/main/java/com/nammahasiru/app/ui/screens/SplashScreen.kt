package com.nammahasiru.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammahasiru.app.R
import com.nammahasiru.app.data.auth.SessionManager
import kotlinx.coroutines.delay

/**
 * Splash screen that shows for ~2 s, then routes to:
 *  - Dashboard  → if user is already logged in (session active)
 *  - Welcome    → if user is not logged in
 */
@Composable
fun SplashScreen(
    onLoggedIn: () -> Unit,
    onNotLoggedIn: () -> Unit
) {
    val context = LocalContext.current
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue   = if (startAnimation) 1f else 0f,
        animationSpec = tween(900),
        label         = "splash_alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue   = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label         = "splash_scale"
    )

    // Subtle pulse after fade-in
    val infiniteAnim = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteAnim.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.06f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label         = "pulse"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2400)
        val session = SessionManager(context)
        if (session.isLoggedIn()) onLoggedIn() else onNotLoggedIn()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF43A047))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(scaleAnim * if (startAnimation) pulse else 1f)
                    .alpha(alphaAnim)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(R.drawable.ic_plant_logo),
                    contentDescription = "Namma Hasiru",
                    modifier           = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text       = "Namma Hasiru",
                fontSize   = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                modifier   = Modifier.alpha(alphaAnim)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text     = "Grow Trees, Grow Future",
                fontSize = 16.sp,
                color    = Color.White.copy(alpha = 0.78f),
                modifier = Modifier.alpha(alphaAnim)
            )
        }
    }
}
