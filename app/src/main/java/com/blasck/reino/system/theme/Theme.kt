package com.blasck.reino.system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = KingdomGoldLight,
    onPrimary = KingdomBrown,
    secondary = Color(0xFFB9CCAF),
    background = KingdomDarkBackground,
    onBackground = KingdomCream,
    surface = KingdomDarkSurface,
    onSurface = KingdomCream,
    surfaceVariant = Color(0xFF3B2B24),
    onSurfaceVariant = Color(0xFFE1CFC2),
    outline = Color(0xFF8E7668),
    error = Color(0xFFFFB4AB),
)

private val LightColorScheme = lightColorScheme(
    primary = KingdomGold,
    onPrimary = Color.White,
    secondary = KingdomGreen,
    background = KingdomCream,
    onBackground = KingdomBrown,
    surface = Color.White,
    onSurface = KingdomBrown,
    surfaceVariant = Color(0xFFF3E7D8),
    onSurfaceVariant = Color(0xFF5E493E),
    outline = Color(0xFF8A7467),
    error = KingdomRed,
)

// Marrom ultra escuro, quase preto com toque marrom
val UltraDarkBrownBackground = Color(0xFF1B0F0A)  // marrom quase preto absoluto
val UltraDarkBrownSurface = Color(0xFF2A160E)     // marrom profundo escuro
val CreamBeigeSurface = Color(0xFFFFF8E1)
// Cinza escuro para contornos
val DarkGrayBorder = Color(0xFF424242)             // cinza escuro

val CustomColorScheme = lightColorScheme(
    primary = CreamBeigeSurface,
    onPrimary = UltraDarkBrownBackground,
    secondary = CreamBeigeSurface,
    onSecondary = UltraDarkBrownBackground,

    background = UltraDarkBrownBackground,
    onBackground = CreamBeigeSurface,

    surface = UltraDarkBrownSurface,
    onSurface = CreamBeigeSurface,

    outline = DarkGrayBorder,
    surfaceVariant = UltraDarkBrownSurface,
    onSurfaceVariant = CreamBeigeSurface,
)

@Composable
fun KingdomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )

}
