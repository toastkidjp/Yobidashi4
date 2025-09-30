package jp.toastkid.yobidashi4.presentation.lib.mouse

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType

class MouseEventAdapter {

    @OptIn(ExperimentalComposeUiApi::class)
    fun isSecondaryClick(event: PointerEvent): Boolean {
        return event.type == PointerEventType.Press
                && event.button == PointerButton.Secondary
    }

}