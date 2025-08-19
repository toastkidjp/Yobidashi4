package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun HoverHighlightDropdownMenuItem(
    labelText: String,
    onClick: () -> Unit
) {
    val cursorOn = remember { mutableStateOf(false) }
    val backgroundColor = animateColorAsState(
        if (cursorOn.value) MaterialTheme.colors.primary
        else Color.Transparent
    )
    val fontColor = animateColorAsState(
        if (cursorOn.value) MaterialTheme.colors.onPrimary
        else Color.Transparent
    )

    DropdownMenuItem(
        onClick = onClick,
        modifier = Modifier
            .drawBehind { drawRect(backgroundColor.value) }
            .onPointerEvent(PointerEventType.Enter) {
                cursorOn.value = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                cursorOn.value = false
            }
    ) {
        Text(labelText, color = fontColor.value)
    }
}
