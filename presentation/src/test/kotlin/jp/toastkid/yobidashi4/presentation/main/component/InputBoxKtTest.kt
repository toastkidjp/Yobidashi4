/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InputBoxKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(InputBoxViewModel::class)
        every { anyConstructed<InputBoxViewModel>().showInputBox() } returns true
        every { anyConstructed<InputBoxViewModel>().start() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun inputBox() {
        val text = "test-input"
        every { anyConstructed<InputBoxViewModel>().query() } returns TextFieldState(text)
        every { anyConstructed<InputBoxViewModel>().invokeAction() } just Runs

        runDesktopComposeUiTest {
            setContent {
                InputBox()
            }

            onNode(hasText(text), useUnmergedTree = true).performImeAction()
            verify { anyConstructed<InputBoxViewModel>().invokeAction() }
        }
    }
}