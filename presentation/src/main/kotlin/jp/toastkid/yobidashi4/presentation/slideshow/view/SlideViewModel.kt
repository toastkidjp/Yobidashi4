package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.KeyEvent
import jp.toastkid.yobidashi4.presentation.lib.keyboard.KeyboardDrivenScrollEventHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class SlideViewModel {

    private val scrollState = ScrollState(0)

    private val focusRequester =  FocusRequester()

    fun scrollState() = scrollState

    private val keyboardDrivenScrollEventHandler = KeyboardDrivenScrollEventHandler()

    private val scrollEventFlow = MutableSharedFlow<Float>(extraBufferCapacity = 1)

    fun scrollEventFlow(): SharedFlow<Float> = scrollEventFlow

    fun keyboardScrollAction(keyEvent: KeyEvent): Boolean {
        val result = keyboardDrivenScrollEventHandler.invoke(keyEvent)
        scrollEventFlow.tryEmit(result.delta)
        return result.consumed
    }

    fun focusRequester() = focusRequester

    fun requestFocus() {
        focusRequester().requestFocus()
    }

}