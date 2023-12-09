package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.math.min
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PreviewKeyEventConsumer(
    private val mainViewModel: MainViewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm
) {

    operator fun invoke(
        it: KeyEvent,
        content: TextFieldValue,
        lastParagraph: MultiParagraph?,
        setNewContent: (TextFieldValue) -> Unit,
        scrollBy: (Float) -> Unit
    ): Boolean {
        if (it.type != KeyEventType.KeyDown) {
            return false
        }
        when {
            it.isCtrlPressed && it.key == Key.DirectionUp -> {
                scrollBy(-16.sp.value)
                return true
            }
            it.isCtrlPressed && it.key == Key.DirectionDown -> {
                scrollBy(16.sp.value)
                return true
            }
            it.isCtrlPressed && it.key == Key.X -> {
                if (content.getSelectedText().isNotEmpty()) {
                    return false
                }

                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                val targetEnd = min(content.text.length, lineEnd + 1)
                val currentLineText = content.text.substring(lineStart, targetEnd)
                ClipboardPutterService().invoke(currentLineText)
                val newText = StringBuilder(content.text)
                    .delete(lineStart, targetEnd)
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(lineStart),
                        content.composition
                    )
                )
                return true
            }
            it.isCtrlPressed && it.key == Key.Enter -> {
                if (content.getSelectedText().isNotEmpty()) {
                    return true
                }

                val textLayoutResult = lastParagraph ?: return false
                val currentLine = textLayoutResult.getLineForOffset(content.selection.start)
                val lineStart = textLayoutResult.getLineStart(currentLine)
                val lineEnd = textLayoutResult.getLineEnd(currentLine)
                val newText = StringBuilder(content.text)
                    .delete(lineStart, lineEnd + 1)
                    .toString()
                setNewContent(
                    TextFieldValue(
                        newText,
                        TextRange(lineStart),
                        content.composition
                    )
                )
                return true
            }
            it.isAltPressed && it.key == Key.DirectionRight -> {
                if (mainViewModel.openArticleList().not()) {
                    mainViewModel.switchArticleList()
                }
                return true
            }
            it.isAltPressed && it.key == Key.DirectionLeft -> {
                mainViewModel.hideArticleList()
                return true
            }
            else -> return false
        }
    }

}