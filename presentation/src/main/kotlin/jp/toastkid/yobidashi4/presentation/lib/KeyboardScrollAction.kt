package jp.toastkid.yobidashi4.presentation.lib

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class KeyboardScrollAction(private val state: ScrollableState) {

    @OptIn(ExperimentalComposeUiApi::class)
    operator fun invoke(coroutineScope: CoroutineScope, key: Key, controlDown: Boolean) = when (key) {
        Key.DirectionUp -> {
            coroutineScope.launch {
                val max = if (controlDown) Float.MAX_VALUE else 50f
                state.animateScrollBy(-1f * max)
            }
            true
        }
        Key.DirectionDown -> {
            coroutineScope.launch {
                val max = if (controlDown) Float.MAX_VALUE else 50f
                state.animateScrollBy(max)
            }
            true
        }
        Key.PageUp -> {
            coroutineScope.launch {
                state.animateScrollBy(-300f)
            }
            true
        }
        Key.PageDown -> {
            coroutineScope.launch {
                state.animateScrollBy(300f)
            }
            true
        }
        else -> false
    }
}