/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CodeBlockViewTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(CodeBlockViewModel::class)
        every { anyConstructed<CodeBlockViewModel>().start(any()) } just Runs
        every { anyConstructed<CodeBlockViewModel>().cursorOn() } just Runs
        every { anyConstructed<CodeBlockViewModel>().cursorOff() } just Runs
        every { anyConstructed<CodeBlockViewModel>().clipContent() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runComposeUiTest {
            setContent {
                CodeBlockView(
                    CodeBlockLine("test\ntest2")
                )
            }

            onNode(hasContentDescription("Clip this code."))
                .performMouseInput {
                    enter()
                    exit()
                    enter()
                    click()
                }

            verify { anyConstructed<CodeBlockViewModel>().cursorOn() }
            verify { anyConstructed<CodeBlockViewModel>().cursorOff() }
            verify { anyConstructed<CodeBlockViewModel>().clipContent() }
        }
    }

}