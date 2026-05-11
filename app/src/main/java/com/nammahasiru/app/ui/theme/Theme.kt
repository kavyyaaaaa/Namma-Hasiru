package com.nammahasiru.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NammaHasiruColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = SurfaceWhite,
    primaryContainer = LightGreen,
    onPrimaryContainer = PrimaryGreenDark,
    secondary = AccentLeaf,
    onSecondary = DarkText,
    tertiary = EarthBrown,
    onTertiary = SurfaceWhite,
    background = Background,
    onBackground = DarkText,
    surface = SurfaceWhite,
    onSurface = DarkText,
    error = AlertRed,
    onError = SurfaceWhite
)

@Composable
fun NammaHasiruTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NammaHasiruColorScheme,
        typography = Typography,
        content = content
    )
}
