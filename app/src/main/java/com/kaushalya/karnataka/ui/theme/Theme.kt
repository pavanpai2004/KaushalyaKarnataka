package com.kaushalya.karnataka.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// PRD Color Palette
val PrimaryBlue = Color(0xFF2563EB)
val PrimaryBlueDark = Color(0xFF1D4ED8)
val PrimaryBlueLight = Color(0xFFDBEAFE)
val SecondarySaffron = Color(0xFFF59E0B)
val SecondarySaffronLight = Color(0xFFFEF3C7)
val AccentGreen = Color(0xFF10B981)
val AccentGreenLight = Color(0xFFD1FAE5)
val BackgroundLight = Color(0xFFF8FAFC)
val TextDark = Color(0xFF1F2937)
val TextMedium = Color(0xFF6B7280)
val SurfaceWhite = Color(0xFFFFFFFF)
val ErrorRed = Color(0xFFEF4444)

private val KaushalyaColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlueDark,

    secondary = SecondarySaffron,
    onSecondary = Color.White,
    secondaryContainer = SecondarySaffronLight,
    onSecondaryContainer = Color(0xFF92400E),

    tertiary = AccentGreen,
    onTertiary = Color.White,
    tertiaryContainer = AccentGreenLight,
    onTertiaryContainer = Color(0xFF065F46),

    background = BackgroundLight,
    surface = SurfaceWhite,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextMedium,

    onBackground = TextDark,
    onSurface = TextDark,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),

    outline = Color(0xFFD1D5DB),
    outlineVariant = Color(0xFFE5E7EB)
)

private val KaushalyaTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        color = TextDark
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        color = TextDark
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        color = TextDark
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)

@Composable
fun KaushalyaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KaushalyaColorScheme,
        typography = KaushalyaTypography,
        content = content
    )
}
