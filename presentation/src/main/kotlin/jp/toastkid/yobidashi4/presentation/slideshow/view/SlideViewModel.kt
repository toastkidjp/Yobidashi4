/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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