/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import kotlinx.coroutines.CoroutineScope

class ConverterToolTabViewModel {

    private val state = ScrollState(0)

    fun scrollState() = state

    private val keyboardScrollAction = KeyboardScrollAction(state)

    fun keyboardScrollAction(coroutineScope: CoroutineScope, key: Key, isCtrlPressed: Boolean): Boolean {
        return this.keyboardScrollAction.invoke(coroutineScope, key, isCtrlPressed)
    }

    private val focusRequester = FocusRequester()

    fun focusRequester() = focusRequester

    fun launch() {
        focusRequester().requestFocus()
    }

}