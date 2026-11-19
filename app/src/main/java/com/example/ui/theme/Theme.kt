package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val RahabarLightColorScheme = lightColorScheme(
    primary = IslamicNavy,
    onPrimary = PureWhite,
    primaryContainer = IslamicNavyContainer,
    onPrimaryContainer = WarmCream,
    secondary = IslamicGold,
    onSecondary = IslamicNavyDeep,
    secondaryContainer = IslamicGoldLight,
    onSecondaryContainer = IslamicNavyDeep,
    tertiary = IslamicEmerald,
    onTertiary = PureWhite,
    tertiaryContainer = IslamicEmeraldSoft,
    onTertiaryContainer = IslamicEmeraldDeep,
    background = WarmCream,
    onBackground = TextOnLightPrimary,
    surface = WarmCreamSurface,
    onSurface = TextOnLightPrimary,
    surfaceVariant = ColorTokens.WarmCardSurface,
    onSurfaceVariant = TextOnLightSecondary,
    outline = WarmCreamBorder,
    outlineVariant = IslamicGold.copy(alpha = 0.35f),
    error = StatusRejected,
    onError = PureWhite
)

private val RahabarDarkColorScheme = darkColorScheme(
    primary = IslamicGold,
    onPrimary = IslamicNavyDeep,
    primaryContainer = IslamicNavyContainer,
    onPrimaryContainer = IslamicGoldLight,
    secondary = IslamicGoldLight,
    onSecondary = IslamicNavyDeep,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = IslamicGoldLight,
    tertiary = ColorTokens.EmeraldHighlight,
    onTertiary = DarkBackground,
    tertiaryContainer = IslamicEmeraldDeep,
    onTertiaryContainer = IslamicEmeraldSoft,
    background = DarkBackground,
    onBackground = TextOnDarkPrimary,
    surface = DarkSurface,
    onSurface = TextOnDarkPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextOnDarkSecondary,
    outline = ColorTokens.DarkBorder,
    outlineVariant = IslamicGold.copy(alpha = 0.35f),
    error = ColorTokens.DarkError,
    onError = DarkBackground
)

private object ColorTokens {
    val WarmCardSurface = androidx.compose.ui.graphics.Color(0xFFF1EBDD)
    val DarkBorder = androidx.compose.ui.graphics.Color(0xFF28394B)
    val EmeraldHighlight = androidx.compose.ui.graphics.Color(0xFF4BB67D)
    val DarkError = androidx.compose.ui.graphics.Color(0xFFFF8A80)
}

val RahabarShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun RahabarTheme(
    darkTheme: Boolean = false,
    isBengali: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RahabarDarkColorScheme else RahabarLightColorScheme
    val typography = buildRahabarTypography(isBengali = isBengali)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = RahabarShapes,
        content = content
    )
}
