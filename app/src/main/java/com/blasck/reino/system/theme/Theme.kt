package com.blasck.reino.system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// Marrom ultra escuro, quase preto com toque marrom
val UltraDarkBrownBackground = Color(0xFF1B0F0A)  // marrom quase preto absoluto
val UltraDarkBrownSurface = Color(0xFF2A160E)     // marrom profundo escuro
val CreamBeigeSurface = Color(0xFFFFF8E1)
// Cinza escuro para contornos
val DarkGrayBorder = Color(0xFF3B201C)             // cinza escuro

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
        colorScheme = CustomColorScheme,
        typography = Typography,
        content = content
    )

}