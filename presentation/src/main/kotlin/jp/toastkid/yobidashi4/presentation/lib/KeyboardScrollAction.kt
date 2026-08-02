/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.lib

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.input.key.Key
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class KeyboardScrollAction(private val state: ScrollableState) {

    operator fun invoke(coroutineScope: CoroutineScope, key: Key, controlDown: Boolean) = when (key) {
        Key.DirectionUp -> {
            coroutineScope.launch {
                if (controlDown && state is LazyListState) {
                    state.scrollToItem(0)
                    return@launch
                }

                val max = if (controlDown) Float.MAX_VALUE else 50f
                state.animateScrollBy(-1f * max)
            }
            true
        }
        Key.DirectionDown -> {
            coroutineScope.launch {
                if (controlDown && state is LazyListState) {
                    state.scrollToItem(state.layoutInfo.totalItemsCount)
                    return@launch
                }

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