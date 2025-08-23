package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun HoverHighlightDropdownMenuItem(
    labelText: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    drawableResource: DrawableResource? = null,
    iconSize: Dp = 24.dp,
    onClick: () -> Unit
) {
    val cursorOn = remember { mutableStateOf(false) }
    val backgroundColor = animateColorAsState(
        if (cursorOn.value) MaterialTheme.colors.primary
        else Color.Transparent
    )
    val fontColor = animateColorAsState(
        if (cursorOn.value) MaterialTheme.colors.onPrimary
        else MaterialTheme.colors.onSurface
    )

    DropdownMenuItem(
        onClick = onClick,
        modifier = modifier
            .drawBehind { drawRect(backgroundColor.value) }
            .onPointerEvent(PointerEventType.Enter) {
                cursorOn.value = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                cursorOn.value = false
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            if (drawableResource != null) {
                Icon(
                    painter = painterResource(drawableResource),
                    contentDescription = labelText,
                    tint = fontColor.value,
                    modifier = Modifier.size(iconSize)
                )
            }

            Text(labelText, fontSize = fontSize, color = fontColor.value, modifier = Modifier.padding(start = if (drawableResource != null) 4.dp else 0.dp))
        }
    }
}
