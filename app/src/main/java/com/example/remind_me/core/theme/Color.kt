package com.example.remind_me.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Material3 Base Tokens
val TealPrimaryLight = Color(0xFF00838F)
val TealSecondaryLight = Color(0xFF006064)
val TealTertiaryLight = Color(0xFF0288D1)

val TealPrimaryDark = Color(0xFF26C6DA)
val TealSecondaryDark = Color(0xFF4DD0E1)
val TealTertiaryDark = Color(0xFF80DEEA)

@Immutable
data class RemindMeColorPalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceMuted: Color,
    val selectedSurface: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val bannerBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val divider: Color,
    val warningContainer: Color,
    val warningText: Color,
    val streakContainer: Color,
    val streakText: Color,
    val successContainer: Color,
    val successBorder: Color,
    val errorContainer: Color,
    val errorBorder: Color
)

val LightRemindMeColors = RemindMeColorPalette(
    isDark = false,
    background = Color(0xFFF5F8FA),
    surface = Color(0xFFFFFFFF),
    surfaceMuted = Color(0xFFF1F5F9),
    selectedSurface = Color(0xFFF0FDFA),
    primary = Color(0xFF00838F),
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF00838F),
    bannerBackground = Color(0xFF00838F),
    textPrimary = Color(0xFF0A2540),
    textSecondary = Color(0xFF64748B),
    border = Color(0xFFE2E8F0),
    divider = Color(0xFFE2E8F0),
    warningContainer = Color(0xFFFFF8E1),
    warningText = Color(0xFF5D4037),
    streakContainer = Color(0xFFFFF3E0),
    streakText = Color(0xFFE65100),
    successContainer = Color(0xFFE8F5E9),
    successBorder = Color(0xFF4CAF50),
    errorContainer = Color(0xFFFFEBEE),
    errorBorder = Color(0xFFEF5350)
)

val DarkRemindMeColors = RemindMeColorPalette(
    isDark = true,
    background = Color(0xFF0B1120),
    surface = Color(0xFF1E293B),
    surfaceMuted = Color(0xFF0F172A),
    selectedSurface = Color(0xFF0F2937),
    primary = Color(0xFF26C6DA),
    primaryContainer = Color(0xFF00363D),
    onPrimaryContainer = Color(0xFF80DEEA),
    bannerBackground = Color(0xFF0D4F58),
    textPrimary = Color(0xFFF1F5F9),
    textSecondary = Color(0xFF94A3B8),
    border = Color(0xFF334155),
    divider = Color(0xFF1E293B),
    warningContainer = Color(0xFF3B2A10),
    warningText = Color(0xFFFFE082),
    streakContainer = Color(0xFF3E2723),
    streakText = Color(0xFFFFAB40),
    successContainer = Color(0xFF133321),
    successBorder = Color(0xFF66BB6A),
    errorContainer = Color(0xFF3B181B),
    errorBorder = Color(0xFFEF5350)
)

val LocalRemindMeColors = staticCompositionLocalOf { LightRemindMeColors }
