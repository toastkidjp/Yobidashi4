package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.presentation.editor.usecase.TextEditorOperationUseCase

class PreviewKeyEventHandler(
    private val useCase: TextEditorOperationUseCase
) {

    operator fun invoke(
        it: KeyEvent,
    ): Boolean {
        if (it.type != KeyEventType.KeyDown) {
            return false
        }
        when {
            it.isShiftPressed && it.isCtrlPressed && it.key == Key.DirectionUp -> {
                useCase.moveToTop()
                return true
            }
            it.isShiftPressed && it.isCtrlPressed && it.key == Key.DirectionDown -> {
                useCase.moveToBottom()
                return true
            }
            it.isCtrlPressed && it.key == Key.DirectionUp -> {
                useCase.scrollBy(-16.sp.value)
                return true
            }
            it.isCtrlPressed && it.key == Key.DirectionDown -> {
                useCase.scrollBy(16.sp.value)
                return true
            }
            it.isCtrlPressed && it.key == Key.X -> {
                return useCase.cutLine()
            }
            it.isCtrlPressed && it.key == Key.Enter -> {
                useCase.deleteLine()
                return true
            }
            it.isCtrlPressed && it.isAltPressed && it.key == Key.DirectionRight -> {
                useCase.switchArticleList()
                return true
            }
            it.isCtrlPressed && it.isAltPressed && it.key == Key.DirectionLeft -> {
                useCase.hideArticleList()
                return true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.L -> {
                useCase.switchLineNumber()
                return true
            }
            else -> return false
        }
    }

}
