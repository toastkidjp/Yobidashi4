package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

class PreviewKeyEventConsumer(
    private val mainViewModel: MainViewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm
) {

    operator fun invoke(
        it: KeyEvent,
        content: TextFieldState,
        lastParagraph: MultiParagraph?,
        scrollBy: (Float) -> Unit
    ): Boolean {
        if (it.type != KeyEventType.KeyDown) {
            return false
        }
        when {
            it.isShiftPressed && it.isCtrlPressed && it.key == Key.DirectionUp -> {
                content.edit {
                    selection = TextRange.Zero
                }
                return true
            }
            it.isShiftPressed && it.isCtrlPressed && it.key == Key.DirectionDown -> {
                content.edit {
                    selection = TextRange(length)
                }
                return true
            }
            it.isCtrlPressed && it.key == Key.DirectionUp -> {
                scrollBy(-16.sp.value)
                return true
            }
            it.isCtrlPressed && it.key == Key.DirectionDown -> {
                scrollBy(16.sp.value)
                return true
            }
            it.isCtrlPressed && it.key == Key.X -> {
                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                val targetEnd = min(content.text.length, lineEnd + 1)
                val currentLineText = content.text.substring(lineStart, targetEnd)
                ClipboardPutterService().invoke(currentLineText)
                content.edit { delete(lineStart, targetEnd) }
                return true
            }
            it.isCtrlPressed && it.key == Key.Enter -> {
                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                content.edit {
                    delete(lineStart, lineEnd + 1)
                }
                return true
            }
            it.isCtrlPressed && it.isAltPressed && it.key == Key.DirectionRight -> {
                if (mainViewModel.openArticleList().not()) {
                    mainViewModel.switchArticleList()
                }
                return true
            }
            it.isCtrlPressed && it.isAltPressed && it.key == Key.DirectionLeft -> {
                mainViewModel.hideArticleList()
                return true
            }
            else -> return false
        }
    }

}
