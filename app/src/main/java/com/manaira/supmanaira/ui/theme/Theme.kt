package com.manaira.supmanaira.ui.theme
import androidx.compose.ui.graphics.Color

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/* ================================================================
   LIGHT THEME — MANAÍRA
   ================================================================ */

private val LightColorScheme = lightColorScheme(
    primary = BlueManaira,
    onPrimary = WhiteText,

    secondary = BlueManairaDark,
    onSecondary = WhiteText,

    background = Color(0xFFF6F7F9),
    onBackground = Color(0xFF1C1C1C),

    surface = Color.White,
    onSurface = Color(0xFF1C1C1C),

    surfaceVariant = Color(0xFFE3F2FD),
    onSurfaceVariant = Color(0xFF5F6368),

    outline = Color(0xFFDDDDDD)
)

/* ================================================================
   DARK THEME — MANAÍRA
   ================================================================ */

private val DarkColorScheme = darkColorScheme(
    primary = BlueManaira,
    onPrimary = WhiteText,

    secondary = BlueManairaDark,
    onSecondary = WhiteText,

    background = Color(0xFF0E0E0E),
    onBackground = WhiteText,

    surface = Color(0xFF121212),
    onSurface = WhiteText,

    surfaceVariant = Color(0xFF263238),
    onSurfaceVariant = GrayText,

    outline = Color(0xFF444444)
)

/* ================================================================
   TEMA PRINCIPAL
   ================================================================ */

@Composable
fun SupManairaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 🔒 correto, NÃO ligue
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
