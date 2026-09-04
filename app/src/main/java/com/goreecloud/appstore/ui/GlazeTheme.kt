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

/** GLAZE UI V1.1 source mapping. Atmosphere never carries semantic state. */
object GlazeAtmosphere {
    val DeepTeal = Color(0xFF0F6B6F)
    val MineralTeal = Color(0xFF1C8A8D)
    val SoftAqua = Color(0xFF8FD6D2)
    val SoftAmber = Color(0xFFD9A35F)
    val ChampagneGold = Color(0xFFE7C78A)
}

private val LightColors = lightColorScheme(
    primary = Color(0xFF3478F6),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0x1F3478F6),
    onPrimaryContainer = Color(0xFF151A23),
    secondary = Color(0xFF7657F6),
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF151A23),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF151A23),
    surfaceVariant = Color(0xE0FFFFFF),
    onSurfaceVariant = Color(0xFF5D6675),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8DB5FF),
    onPrimary = Color(0xFF0B0D11),
    primaryContainer = Color(0x1F8DB5FF),
    onPrimaryContainer = Color(0xFFF5F7FA),
    secondary = Color(0xFFA990FF),
    background = Color(0xFF0B0D11),
    onBackground = Color(0xFFF5F7FA),
    surface = Color(0xFF12151B),
    onSurface = Color(0xFFF5F7FA),
    surfaceVariant = Color(0xDB181D26),
    onSurfaceVariant = Color(0xFFB0B7C3),
)

private val DeepDarkColors = darkColorScheme(
    primary = Color(0xFF8DB5FF),
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
