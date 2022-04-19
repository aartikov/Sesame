package me.aartikov.sesamecomposesample.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Red,
    primaryVariant = DarkRed,
    secondary = Green,
    surface = Gray
)

private val LightColorPalette = lightColors(
    primary = Red,
    primaryVariant = DarkRed,
    secondary = Green,
    surface = Gray
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}