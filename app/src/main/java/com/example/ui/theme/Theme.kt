package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GamePrimary,
    secondary = GameSecondary,
    tertiary = GameTertiary,
    background = GameBackground,
    surface = GameSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFF3F4F6),
    onSurface = Color(0xFFF3F4F6),
    surfaceVariant = GameSurfaceLight,
    onSurfaceVariant = Color(0xFF9CA3AF)
)

private val LightColorScheme = lightColorScheme(
    primary = GamePrimary,
    secondary = GameSecondary,
    tertiary = GameTertiary,
    background = Color(0xFFF9FAFB),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to stunning immersive dark theme
  dynamicColor: Boolean = false, // Disable dynamic color to enforce our styled gaming vibes
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
