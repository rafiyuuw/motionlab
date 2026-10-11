package com.example.motionlab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Motion Lab Minimalist Color Palette
val WarmBackground = Color(0xFFF9F8F5)
val SurfaceCard = Color(0xFFFFFFFF)
val CharcoalText = Color(0xFF1E2124)
val CharcoalSecondary = Color(0xFF4B5158)
val MutedText = Color(0xFF727880)
val SubtleBorder = Color(0xFFE2E0D8)

val MutedGreen = Color(0xFF385E48)
val MutedGreenContainer = Color(0xFFEAF1EC)
val MutedGreenText = Color(0xFF223F2F)

val AccentAmber = Color(0xFFC06C11)
val AmberContainer = Color(0xFFFDF4E7)

val AccentRed = Color(0xFFBA2D2D)
val RedContainer = Color(0xFFFCEAEA)

val GridLineColor = Color(0xFF385E48).copy(alpha = 0.22f)
val GridLineMain = Color(0xFF385E48).copy(alpha = 0.45f)
val HorizonLineColor = Color(0xFFC06C11).copy(alpha = 0.60f)

private val LightColorScheme = lightColorScheme(
    primary = MutedGreen,
    onPrimary = Color.White,
    primaryContainer = MutedGreenContainer,
    onPrimaryContainer = MutedGreenText,
    secondary = CharcoalSecondary,
    onSecondary = Color.White,
    background = WarmBackground,
    onBackground = CharcoalText,
    surface = SurfaceCard,
    onSurface = CharcoalText,
    surfaceVariant = WarmBackground,
    onSurfaceVariant = CharcoalSecondary,
    outline = SubtleBorder
)

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = CharcoalText,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = CharcoalText,
        letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        color = CharcoalText
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = CharcoalText
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = CharcoalSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 1.0.sp,
        color = MutedText
    )
)

@Composable
fun MotionLabTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
