package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent

class MarkdownPreviewViewModel(scrollState: ScrollableState) : KoinComponent {

    private val scrollAction = KeyboardScrollAction(scrollState)

    fun onKeyEvent(coroutineScope: CoroutineScope, it: KeyEvent): Boolean {
        return scrollAction(coroutineScope, it.key, it.isCtrlPressed)
    }

}
