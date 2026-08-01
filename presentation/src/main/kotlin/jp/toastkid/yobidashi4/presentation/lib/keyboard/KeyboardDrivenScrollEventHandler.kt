/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.lib.keyboard

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key

class KeyboardDrivenScrollEventHandler {
    
    operator fun invoke(it: KeyEvent): KeyboardDrivenScrollResult {
        return when (it.key) {
            Key.DirectionUp -> {
                val max = if (it.isCtrlPressed) Float.MAX_VALUE else 50f
                KeyboardDrivenScrollResult(-1f * max, true)
            }
            Key.DirectionDown -> {
                val max = if (it.isCtrlPressed) Float.MAX_VALUE else 50f
                KeyboardDrivenScrollResult(max, true)
            }
            Key.PageUp -> {
                KeyboardDrivenScrollResult(-300f, true)
            }
            Key.PageDown -> {
                KeyboardDrivenScrollResult(300f, true)
            }
            else -> KeyboardDrivenScrollResult(-1f, false)
        }
    }
    
}