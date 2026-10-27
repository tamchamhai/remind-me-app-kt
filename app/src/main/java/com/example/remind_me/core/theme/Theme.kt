package com.example.remind_me.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.remind_me.core.localization.AppStrings
import com.example.remind_me.core.localization.LocalAppStrings
import com.example.remind_me.core.localization.getAppStrings

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryDark,
    secondary = TealSecondaryDark,
    tertiary = TealTertiaryDark,
    background = DarkRemindMeColors.background,
    surface = DarkRemindMeColors.surface,
    onPrimary = Color(0xFF00363D),
    onBackground = DarkRemindMeColors.textPrimary,
    onSurface = DarkRemindMeColors.textPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimaryLight,
    secondary = TealSecondaryLight,
    tertiary = TealTertiaryLight,
    background = LightRemindMeColors.background,
    surface = LightRemindMeColors.surface,
    onPrimary = Color.White,
    onBackground = LightRemindMeColors.textPrimary,
    onSurface = LightRemindMeColors.textPrimary
)

object RemindMeTheme {
    val colors: RemindMeColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalRemindMeColors.current

    val strings: AppStrings
        @Composable
        @ReadOnlyComposable
        get() = LocalAppStrings.current
}

@Composable
fun RemindmeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    languageCode: String = "vi",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColors = if (darkTheme) DarkRemindMeColors else LightRemindMeColors
    val appStrings = getAppStrings(languageCode)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalRemindMeColors provides customColors,
        LocalAppStrings provides appStrings
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
