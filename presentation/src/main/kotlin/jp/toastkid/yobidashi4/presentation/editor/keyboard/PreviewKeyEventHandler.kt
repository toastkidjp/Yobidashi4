/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.presentation.editor.usecase.TextEditorOperationUseCase

class PreviewKeyEventHandler(
    private val useCase: TextEditorOperationUseCase
) {

    operator fun invoke(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyEventType.KeyDown) {
            return false
        }
        when {
            keyEvent.isShiftPressed && keyEvent.isCtrlPressed && keyEvent.key == Key.DirectionUp -> {
                useCase.moveToTop()
                return true
            }
            keyEvent.isShiftPressed && keyEvent.isCtrlPressed && keyEvent.key == Key.DirectionDown -> {
                useCase.moveToBottom()
                return true
            }
            keyEvent.isCtrlPressed && keyEvent.key == Key.DirectionUp -> {
                useCase.scrollBy(-16.sp.value)
                return true
            }
            keyEvent.isCtrlPressed && keyEvent.key == Key.DirectionDown -> {
                useCase.scrollBy(16.sp.value)
                return true
            }
            keyEvent.isCtrlPressed && keyEvent.key == Key.X -> {
                return useCase.cutLine()
            }
            keyEvent.isCtrlPressed && keyEvent.key == Key.Enter -> {
                useCase.deleteLine()
                return true
            }
            keyEvent.isCtrlPressed && keyEvent.isAltPressed && keyEvent.key == Key.DirectionRight -> {
                useCase.switchArticleList()
                return true
            }
            keyEvent.isCtrlPressed && keyEvent.isAltPressed && keyEvent.key == Key.DirectionLeft -> {
                useCase.hideArticleList()
                return true
            }
            keyEvent.isCtrlPressed && keyEvent.isShiftPressed && keyEvent.key == Key.L -> {
                useCase.switchLineNumber()
                return true
            }
            else -> return false
        }
    }

}
