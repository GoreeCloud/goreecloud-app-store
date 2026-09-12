package com.goreecloud.appstore.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class GlazeThemeMode { SYSTEM, LIGHT, DARK, DEEP_DARK }

/**
 * GLAZE UI V1.3 — Adaptive Resonance source mapping.
 *
 * Neutral glass remains the material foundation. Adaptive expression is bounded,
 * non-semantic, and subordinate to accessibility and task completion. The App Store
 * does not sample environment/user content or use adaptive color as authority for
 * entitlement, trust, privacy, security, installation, or availability state.
 */
object GlazeFoundation {
    val FrostWhite = Color(0xFFF7F9FC)
    val Pearl = Color(0xFFEFF2F6)
    val IceBlue = Color(0xFF8DB5FF)
    val Smoke = Color(0xFFB0B7C3)
    val Graphite = Color(0xFF151A23)
}

private val LightColors = lightColorScheme(
    primary = Color(0xFF3478F6),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0x1F3478F6),
    onPrimaryContainer = GlazeFoundation.Graphite,
    secondary = Color(0xFF7657F6),
    background = GlazeFoundation.FrostWhite,
    onBackground = GlazeFoundation.Graphite,
    surface = Color(0xFFFFFFFF),
    onSurface = GlazeFoundation.Graphite,
    surfaceVariant = Color(0xE8EFF2F6),
    onSurfaceVariant = Color(0xFF5D6675),
)

private val DarkColors = darkColorScheme(
    primary = GlazeFoundation.IceBlue,
    onPrimary = Color(0xFF0B0D11),
    primaryContainer = Color(0x1F8DB5FF),
    onPrimaryContainer = Color(0xFFF5F7FA),
    secondary = Color(0xFFA990FF),
    background = Color(0xFF0B0D11),
    onBackground = Color(0xFFF5F7FA),
    surface = Color(0xFF12151B),
    onSurface = Color(0xFFF5F7FA),
    surfaceVariant = Color(0xE3181D26),
    onSurfaceVariant = GlazeFoundation.Smoke,
)

private val DeepDarkColors = darkColorScheme(
    primary = GlazeFoundation.IceBlue,
    onPrimary = Color(0xFF05070A),
    primaryContainer = Color(0x1F8DB5FF),
    onPrimaryContainer = Color(0xFFF5F7FA),
    secondary = Color(0xFFA990FF),
    background = Color(0xFF05070A),
    onBackground = Color(0xFFF5F7FA),
    surface = Color(0xFF0D1015),
    onSurface = Color(0xFFF5F7FA),
    surfaceVariant = Color(0xE612161D),
    onSurfaceVariant = Color(0xFFABB4C2),
)

val GlazeCardShape = RoundedCornerShape(24.dp)
val GlazeSmallCardShape = RoundedCornerShape(16.dp)
val GlazeArtworkShape = RoundedCornerShape(16.dp)
val GlazeCapsuleShape = RoundedCornerShape(999.dp)

@Composable
fun GlazeTheme(
    mode: GlazeThemeMode = GlazeThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val scheme = when (mode) {
        GlazeThemeMode.SYSTEM -> if (isSystemInDarkTheme()) DarkColors else LightColors
        GlazeThemeMode.LIGHT -> LightColors
        GlazeThemeMode.DARK -> DarkColors
        GlazeThemeMode.DEEP_DARK -> DeepDarkColors
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
