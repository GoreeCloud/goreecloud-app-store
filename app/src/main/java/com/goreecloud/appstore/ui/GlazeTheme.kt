package com.goreecloud.appstore.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Native Glaze UI 2.1 material-role mapping for the App Store development client.
 *
 * Content planes stay solid. Interaction chrome uses the Glaze material roles. This mapping is
 * an adoption candidate only; it is not application conformance or physical-device acceptance.
 */
data class GlazeMaterialRoles(
    val canvas: Color,
    val surface: Color,
    val raisedSurface: Color,
    val softGlaze: Color,
    val glaze: Color,
    val accent: Color,
    val accentSoft: Color,
    val border: Color,
    val positive: Color,
    val warning: Color,
)

private val LightGlazeRoles = GlazeMaterialRoles(
    canvas = Color(0xFFF4F7FD),
    surface = Color(0xFFFFFFFF),
    raisedSurface = Color(0xFFF9FBFF),
    softGlaze = Color(0xFFF0F3FF),
    glaze = Color(0xFFE8EEFF),
    accent = Color(0xFF3F57D6),
    accentSoft = Color(0xFFE3E9FF),
    border = Color(0xFFD8DFEE),
    positive = Color(0xFF1C7E55),
    warning = Color(0xFF96630C),
)

private val DarkGlazeRoles = GlazeMaterialRoles(
    canvas = Color(0xFF0C0E14),
    surface = Color(0xFF14171F),
    raisedSurface = Color(0xFF1C212C),
    softGlaze = Color(0xFF20283A),
    glaze = Color(0xFF262F43),
    accent = Color(0xFF93A6FF),
    accentSoft = Color(0xFF2D375B),
    border = Color(0xFF40495C),
    positive = Color(0xFF69D8A8),
    warning = Color(0xFFEDC069),
)

private val LightColors = lightColorScheme(
    primary = LightGlazeRoles.accent,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = LightGlazeRoles.accentSoft,
    onPrimaryContainer = Color(0xFF161923),
    secondary = Color(0xFF58617A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = LightGlazeRoles.softGlaze,
    onSecondaryContainer = Color(0xFF252B3A),
    background = LightGlazeRoles.canvas,
    onBackground = Color(0xFF161923),
    surface = LightGlazeRoles.surface,
    onSurface = Color(0xFF161923),
    surfaceVariant = LightGlazeRoles.raisedSurface,
    onSurfaceVariant = Color(0xFF5B6375),
    surfaceContainerLowest = LightGlazeRoles.surface,
    surfaceContainerLow = LightGlazeRoles.raisedSurface,
    surfaceContainer = LightGlazeRoles.glaze,
    surfaceContainerHigh = Color(0xFFE4EAFA),
    surfaceContainerHighest = Color(0xFFDCE4F7),
    outline = LightGlazeRoles.border,
    outlineVariant = Color(0xFFE4E9F3),
)

private val DarkColors = darkColorScheme(
    primary = DarkGlazeRoles.accent,
    onPrimary = Color(0xFF0A0D15),
    primaryContainer = DarkGlazeRoles.accentSoft,
    onPrimaryContainer = Color(0xFFF9FAFF),
    secondary = Color(0xFFBEC8E5),
    onSecondary = Color(0xFF202638),
    secondaryContainer = DarkGlazeRoles.softGlaze,
    onSecondaryContainer = Color(0xFFE8EDFF),
    background = DarkGlazeRoles.canvas,
    onBackground = Color(0xFFF9FAFF),
    surface = DarkGlazeRoles.surface,
    onSurface = Color(0xFFF9FAFF),
    surfaceVariant = DarkGlazeRoles.raisedSurface,
    onSurfaceVariant = Color(0xFFB7BFCF),
    surfaceContainerLowest = DarkGlazeRoles.surface,
    surfaceContainerLow = DarkGlazeRoles.raisedSurface,
    surfaceContainer = DarkGlazeRoles.glaze,
    surfaceContainerHigh = Color(0xFF303A50),
    surfaceContainerHighest = Color(0xFF39445B),
    outline = DarkGlazeRoles.border,
    outlineVariant = Color(0xFF343C4E),
)

private val LocalGlazeMaterialRoles = staticCompositionLocalOf { LightGlazeRoles }

object GlazeMaterials {
    val current: GlazeMaterialRoles
        @Composable get() = LocalGlazeMaterialRoles.current
}

val GlazeCardShape = RoundedCornerShape(28.dp)
val GlazeSmallCardShape = RoundedCornerShape(20.dp)
val GlazeArtworkShape = RoundedCornerShape(18.dp)
val GlazeCapsuleShape = RoundedCornerShape(999.dp)

@Composable
fun GlazeTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    CompositionLocalProvider(LocalGlazeMaterialRoles provides if (dark) DarkGlazeRoles else LightGlazeRoles) {
        MaterialTheme(
            colorScheme = if (dark) DarkColors else LightColors,
            typography = MaterialTheme.typography,
            content = content,
        )
    }
}
