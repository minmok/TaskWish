package com.example.gachaproductiveapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GachaDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF59E0B), // Gold
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF78350F),
    onPrimaryContainer = Color(0xFFFEF3C7),
    secondary = Color(0xFF8B5CF6), // Purple
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    background = Color(0xFF0F172A), // Deep Slate Navy
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    error = Color(0xFFEF4444),
    onError = Color.White
)

@Composable
fun GachaProductiveAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GachaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
