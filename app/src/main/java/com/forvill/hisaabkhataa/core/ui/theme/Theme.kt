package com.forvill.hisaabkhataa.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Clay,
    secondary = Moss,
    tertiary = Gold,
    background = Paper,
    surface = Paper,
    onPrimary = Paper,
    onSecondary = Paper,
    onTertiary = Ink,
    onBackground = Ink,
    onSurface = Ink,
)

private val DarkColors = darkColorScheme(
    primary = Gold,
    secondary = Moss,
    tertiary = Clay,
    background = Ink,
    surface = Ink,
    onPrimary = Ink,
    onSecondary = Paper,
    onTertiary = Paper,
    onBackground = Paper,
    onSurface = Paper,
)

@Composable
fun HisaabKhataaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}
