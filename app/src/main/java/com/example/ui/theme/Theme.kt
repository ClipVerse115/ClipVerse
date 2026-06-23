package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyberPurple,
    secondary = HotPink,
    tertiary = CyberCyan,
    background = CosmicBlack,
    surface = DeepSpaceBlue,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFECEEF5),
    onSurface = Color(0xFFECEEF5),
    secondaryContainer = Color(0x338B5CF6),
    onSecondaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    secondary = SecondaryPink,
    tertiary = CyberCyan,
    background = LightBg,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1F2029),
    onSurface = Color(0xFF1F2029)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Premium mode as requested by user
    dynamicColor: Boolean = false, // Disable dynamic colors to enforce the custom brand palette
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
