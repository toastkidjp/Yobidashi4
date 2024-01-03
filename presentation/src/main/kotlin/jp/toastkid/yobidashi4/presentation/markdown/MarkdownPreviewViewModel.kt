package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MarkdownPreviewViewModel(scrollState: ScrollableState) : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val scrollAction = KeyboardScrollAction(scrollState)

    private val keywordHighlighter = KeywordHighlighter()

    fun onKeyEvent(coroutineScope: CoroutineScope, it: KeyEvent): Boolean {
        val scrollActionConsumed = scrollAction(coroutineScope, it.key, it.isCtrlPressed)
        if (scrollActionConsumed) {
            return true
        }

        if (it.type != KeyEventType.KeyUp) {
            return false
        }

        if (it.isCtrlPressed && it.isShiftPressed && it.key == Key.O) {
            mainViewModel.webSearch(mainViewModel.selectedText())
            return true
        }

        return false
    }

    fun annotate(text: String, finderTarget: String?) = keywordHighlighter(text, finderTarget)

}
