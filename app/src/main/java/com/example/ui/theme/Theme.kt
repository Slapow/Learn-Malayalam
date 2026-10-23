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
    primary = EmeraldPrimaryDark,
    onPrimary = Color(0xFF00381E),
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = Color(0xFFA6F5C5),
    secondary = GoldenSpiceDark,
    onSecondary = Color(0xFF422B00),
    secondaryContainer = Color(0xFF5F3F00),
    onSecondaryContainer = Color(0xFFFFDEA3),
    tertiary = Color(0xFFFFB4A5),
    background = EmeraldDarkBackground,
    surface = EmeraldDarkSurface,
    surfaceVariant = EmeraldDarkSurfaceVariant,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = PalmContainerLight,
    onPrimaryContainer = PalmOnContainerLight,
    secondary = GoldenSpice,
    onSecondary = Color.White,
    secondaryContainer = GoldenContainerLight,
    onSecondaryContainer = GoldenOnContainer,
    tertiary = TerracottaAccent,
    onTertiary = Color.White,
    tertiaryContainer = TerracottaContainer,
    background = SandBackgroundLight,
    surface = SandSurfaceLight,
    surfaceVariant = SandSurfaceVariantLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep warm signature Kerala emerald styling by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
