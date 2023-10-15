package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun VerticalDivider(
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    modifier: Modifier
) {
    Box(
        modifier = modifier.heightIn(28.dp)
            .width(thickness)
            .background(color)
    )
}