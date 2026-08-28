package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalIsDarkTheme = compositionLocalOf { true }

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),       // Radiant Emerald Green
    secondary = Color(0xFFF43F5E),     // Ruby Red
    tertiary = Color(0xFFF59E0B),      // Warm Amber Gold
    background = Color(0xFF080C14),    // Midnight Obsidian Canvas
    surface = Color(0xFF131B2E),       // Elevated Midnight Navy Card
    surfaceVariant = Color(0xFF1E293B),
    onPrimary = Color(0xFF080C14),
    onSecondary = Color.White,
    onTertiary = Color(0xFF080C14),
    onBackground = Color(0xFFF8FAFC),  // Crisp Titanium White
    onSurface = Color(0xFFF8FAFC),
    error = Color(0xFFFB7185)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,            // Emerald Green (0xFF2ECC71)
    secondary = RubyRed,               // Ruby Red (0xFFFF5252)
    tertiary = DesertGold,             // Desert Gold (0xFFFFC300)
    background = AlabasterSand,        // Alabaster Sand (0xFFF4F6F9)
    surface = Color(0xFFFFFFFF),       // Pure White
    surfaceVariant = Color(0xFFE2E8F0),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = SlateBlue,
    onBackground = SlateBlue,          // Soft Navy (0xFF2C3E50)
    onSurface = SlateBlue,
    error = RubyRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to preserve our brand color scheme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

