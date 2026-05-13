package com.nammahasiru.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammahasiru.app.R
import com.nammahasiru.app.ui.theme.*

@Composable
fun WelcomeScreen(
    onSignIn: () -> Unit,
    onRegister: () -> Unit
) {
    // ── Entry animations ──────────────────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }

    val logoAlpha   by animateFloatAsState(if (visible) 1f else 0f, tween(800),           label = "logoAlpha")
    val logoScale   by animateFloatAsState(if (visible) 1f else 0.6f, tween(900, easing = FastOutSlowInEasing), label = "logoScale")
    val titleAlpha  by animateFloatAsState(if (visible) 1f else 0f, tween(800, 300),      label = "titleAlpha")
    val tagAlpha    by animateFloatAsState(if (visible) 1f else 0f, tween(800, 550),      label = "tagAlpha")
    val btnAlpha    by animateFloatAsState(if (visible) 1f else 0f, tween(800, 800),      label = "btnAlpha")

    // Floating animation for the logo
    val infiniteAnim = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteAnim.animateFloat(
        initialValue   = -6f,
        targetValue    = 6f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(2200, easing = SinusoidalEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    LaunchedEffect(Unit) { visible = true }

    // ── Background gradient ───────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20),
                        Color(0xFF2E7D32),
                        Color(0xFF388E3C),
                        Color(0xFF43A047)
                    )
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-60).dp, y = (-60).dp)
                .background(Color.White.copy(alpha = 0.04f), shape = RoundedCornerShape(50))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .background(Color.White.copy(alpha = 0.04f), shape = RoundedCornerShape(50))
        )

        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo ──────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .offset(y = floatOffset.dp)
                    .alpha(logoAlpha)
                    .scale(logoScale)
                    .shadow(12.dp, RoundedCornerShape(50), ambientColor = Color(0xFF1B5E20))
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.ic_plant_logo),
                    contentDescription = "Namma Hasiru Plant Logo",
                    modifier           = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── App name ──────────────────────────────────────────────────────
            Text(
                text       = "Namma Hasiru",
                fontSize   = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                modifier   = Modifier.alpha(titleAlpha),
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Tagline ───────────────────────────────────────────────────────
            Surface(
                color  = Color.White.copy(alpha = 0.15f),
                shape  = RoundedCornerShape(24.dp),
                modifier = Modifier.alpha(tagAlpha)
            ) {
                Text(
                    text       = "🌱  Grow Trees, Grow Future",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color      = Color.White,
                    modifier   = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    textAlign  = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(56.dp))

            // ── Buttons ───────────────────────────────────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .alpha(btnAlpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Sign In
                Button(
                    onClick  = onSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape    = RoundedCornerShape(28.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor   = Color(0xFF2E7D32)
                    ),
                    elevation = ButtonDefaults.buttonElevation(6.dp)
                ) {
                    Text(
                        text       = "Sign In",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Register
                OutlinedButton(
                    onClick  = onRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape    = RoundedCornerShape(28.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border   = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {
                    Text(
                        text       = "Register",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text     = "Plant a tree. Make a difference.",
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.65f),
                modifier = Modifier.alpha(btnAlpha),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Simple sinusoidal easing for floating effect
private val SinusoidalEasing = Easing { fraction -> fraction }
