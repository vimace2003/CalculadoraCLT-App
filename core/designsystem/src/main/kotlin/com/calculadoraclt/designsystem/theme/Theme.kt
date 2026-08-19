package com.calculadoraclt.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CltPrimaryLight,
    onPrimary = CltOnPrimaryLight,
    primaryContainer = CltPrimaryContainerLight,
    onPrimaryContainer = CltOnPrimaryContainerLight,
    secondary = CltSecondaryLight,
    tertiary = CltTertiaryLight,
    background = CltBackgroundLight,
    surface = CltSurfaceLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = CltPrimaryDark,
    onPrimary = CltOnPrimaryDark,
    primaryContainer = CltPrimaryContainerDark,
    onPrimaryContainer = CltOnPrimaryContainerDark,
    secondary = CltSecondaryDark,
    tertiary = CltTertiaryDark,
    background = CltBackgroundDark,
    surface = CltSurfaceDark,
)

@Composable
fun CalculadoraCltTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
        typography = CltTypography,
        shapes = CltShapes,
        content = content,
    )
}
