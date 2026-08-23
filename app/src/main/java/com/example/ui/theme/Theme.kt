package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ElegantDarkColorScheme = darkColorScheme(
    primary = ElegantDarkPrimary,
    onPrimary = ElegantDarkOnPrimary,
    primaryContainer = ElegantDarkPrimaryContainer,
    onPrimaryContainer = ElegantDarkOnPrimaryContainer,
    secondary = ElegantDarkSecondary,
    onSecondary = ElegantDarkOnSecondary,
    secondaryContainer = ElegantDarkSecondaryContainer,
    onSecondaryContainer = ElegantDarkOnSecondaryContainer,
    tertiary = ElegantDarkTertiary,
    onTertiary = ElegantDarkOnTertiary,
    tertiaryContainer = ElegantDarkTertiaryContainer,
    onTertiaryContainer = ElegantDarkOnTertiaryContainer,
    background = ElegantDarkBackground,
    onBackground = ElegantDarkOnBackground,
    surface = ElegantDarkSurface,
    onSurface = ElegantDarkOnSurface,
    surfaceVariant = ElegantDarkSurfaceVariant,
    onSurfaceVariant = ElegantDarkOnSurfaceVariant,
    outline = ElegantDarkOutline,
    outlineVariant = ElegantDarkOutlineVariant,
    error = ElegantDarkError,
    onError = ElegantDarkOnError,
    errorContainer = ElegantDarkErrorContainer,
    onErrorContainer = ElegantDarkOnErrorContainer
)

private val ElegantLightColorScheme = lightColorScheme(
    primary = ElegantLightPrimary,
    onPrimary = ElegantLightOnPrimary,
    primaryContainer = ElegantLightPrimaryContainer,
    onPrimaryContainer = ElegantLightOnPrimaryContainer,
    secondary = ElegantLightSecondary,
    onSecondary = ElegantLightOnSecondary,
    secondaryContainer = ElegantLightSecondaryContainer,
    onSecondaryContainer = ElegantLightOnSecondaryContainer,
    tertiary = ElegantLightTertiary,
    onTertiary = ElegantLightOnTertiary,
    tertiaryContainer = ElegantLightTertiaryContainer,
    onTertiaryContainer = ElegantLightOnTertiaryContainer,
    background = ElegantLightBackground,
    onBackground = ElegantLightOnBackground,
    surface = ElegantLightSurface,
    onSurface = ElegantLightOnSurface,
    surfaceVariant = ElegantLightSurfaceVariant,
    onSurfaceVariant = ElegantLightOnSurfaceVariant,
    outline = ElegantLightOutline,
    outlineVariant = ElegantLightOutlineVariant,
    error = ElegantLightError,
    onError = ElegantLightOnError,
    errorContainer = ElegantLightErrorContainer,
    onErrorContainer = ElegantLightOnErrorContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ElegantDarkColorScheme else ElegantLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

