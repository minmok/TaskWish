package com.example.gachaproductiveapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GachaDarkColorScheme = darkColorScheme(
    primary = PrimaryGold,
    onPrimary = DarkBackground,
    primaryContainer = PrimaryGoldContainer,
    onPrimaryContainer = PrimaryGoldLight,
    secondary = SecondaryPurple,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryPurpleContainer,
    onSecondaryContainer = SecondaryPurpleLight,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = AccentRose,
    onError = TextPrimary
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
