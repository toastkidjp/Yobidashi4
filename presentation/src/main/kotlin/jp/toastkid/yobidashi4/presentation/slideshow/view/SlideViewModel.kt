package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import kotlinx.coroutines.CoroutineScope

class SlideViewModel {

    private val scrollState = ScrollState(0)

    private val keyboardScrollAction =  KeyboardScrollAction(scrollState)

    private val focusRequester =  FocusRequester()

    fun scrollState() = scrollState

    fun keyboardScrollAction(coroutineScope: CoroutineScope, key: Key, isCtrlPressed: Boolean): Boolean {
        return keyboardScrollAction.invoke(coroutineScope, key, isCtrlPressed)
    }

    fun focusRequester() = focusRequester

    fun requestFocus() {
        focusRequester.requestFocus()
    }

}