package com.akrishna87.tomorrowsbag.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Accent2,
    onSecondary = Ink,
    tertiary = Good,
    background = LightBackground,
    onBackground = Ink,
    surface = LightSurface,
    onSurface = Ink,
    surfaceVariant = Color(0xFFF2F2F2),
    onSurfaceVariant = Muted
)

private val DarkScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Accent2,
    onSecondary = Ink,
    tertiary = Good,
    background = DarkBackground,
    onBackground = Color(0xFFF2EDE6),
    surface = DarkSurface,
    onSurface = Color(0xFFF2EDE6),
    surfaceVariant = Color(0xFF42372B),
    onSurfaceVariant = Color(0xFFBDB3A6)
)

@Composable
fun TomorrowsBagTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) DarkScheme else LightScheme
    MaterialTheme(colorScheme = colorScheme, content = content)
}
