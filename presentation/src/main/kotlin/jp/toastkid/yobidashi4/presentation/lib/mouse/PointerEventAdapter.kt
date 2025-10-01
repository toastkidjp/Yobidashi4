/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.lib.mouse

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType

class PointerEventAdapter {

    @OptIn(ExperimentalComposeUiApi::class)
    fun isSecondaryClick(event: PointerEvent): Boolean {
        return event.type == PointerEventType.Press
                && event.button == PointerButton.Secondary
    }

}