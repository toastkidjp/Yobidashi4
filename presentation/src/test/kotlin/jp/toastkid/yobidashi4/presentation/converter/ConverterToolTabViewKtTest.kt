/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performKeyPress
import androidx.compose.ui.test.runDesktopComposeUiTest
import org.junit.jupiter.api.Test

class ConverterToolTabViewKtTest {

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun converterToolTabView() {
        runDesktopComposeUiTest {
            setContent {
                ConverterToolTabView()
            }

            val node = onNode(hasContentDescription("surface"), useUnmergedTree = true)
            node.performKeyPress(KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true))
            node.performKeyInput {
                keyDown(Key.DirectionDown)
                keyUp(Key.DirectionDown)
            }
        }
    }
}