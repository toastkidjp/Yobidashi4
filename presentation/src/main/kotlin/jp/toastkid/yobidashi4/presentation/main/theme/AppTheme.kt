package jp.toastkid.yobidashi4.main

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable  () -> Unit
) {
    val primary = Color(0xFF444499)
    val onPrimary = Color(0xFFEECCAA)

    val colors =
        if (darkTheme)
            darkColors(
                primary = primary,
                secondary = onPrimary,
                surface = Color(0xFF0F0F0F),
                background = Color(0xFF0F0F0F),
                onPrimary = onPrimary,
                onSurface = Color(0xFFF0F0F0),
                onBackground = Color(0xFFF0F0F0)
            )
        else
            lightColors(
                primary = primary,
                secondary = primary,
                surface = Color(0xFFF0F0F0),
                background = Color(0xFFF0F0F0),
                onPrimary = onPrimary,
                onSurface = Color(0xFF000B00),
                onBackground = Color(0xFF000B00)
            )

    MaterialTheme(colors = colors) {
        CompositionLocalProvider(LocalTextSelectionColors provides TextSelectionColors(
            handleColor = MaterialTheme.colors.secondary,
            backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.65f)
        )) {
            content()
        }
    }
}