package com.goreecloud.appstore.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF315DA8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE6EEFF),
    onPrimaryContainer = Color(0xFF0D2B5B),
    secondary = Color(0xFF62527A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF0E9FA),
    onSecondaryContainer = Color(0xFF2B1F3D),
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF191C20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFEEF1F7),
    onSurfaceVariant = Color(0xFF454A53),
    outline = Color(0xFF747981),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAAC7FF),
    onPrimary = Color(0xFF003061),
    primaryContainer = Color(0xFF17477E),
    onPrimaryContainer = Color(0xFFD7E2FF),
    secondary = Color(0xFFD2BFE8),
    onSecondary = Color(0xFF382D49),
    secondaryContainer = Color(0xFF4F435F),
    onSecondaryContainer = Color(0xFFEEDBFF),
    background = Color(0xFF101319),
    onBackground = Color(0xFFE2E2E8),
    surface = Color(0xFF171A21),
    onSurface = Color(0xFFE2E2E8),
    surfaceVariant = Color(0xFF242A35),
    onSurfaceVariant = Color(0xFFC5C8D0),
    outline = Color(0xFF8F939C),
)

val GlazeCardShape = RoundedCornerShape(28.dp)
val GlazeSmallCardShape = RoundedCornerShape(20.dp)
val GlazeArtworkShape = RoundedCornerShape(18.dp)
val GlazeCapsuleShape = RoundedCornerShape(999.dp)

@Composable
fun GlazeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}
