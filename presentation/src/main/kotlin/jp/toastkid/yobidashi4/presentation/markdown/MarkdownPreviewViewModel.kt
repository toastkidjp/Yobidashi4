package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
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
        return scrollAction(coroutineScope, it.key, it.isCtrlPressed)
    }

    fun annotate(text: String, finderTarget: String?) = keywordHighlighter(text, finderTarget)

}
