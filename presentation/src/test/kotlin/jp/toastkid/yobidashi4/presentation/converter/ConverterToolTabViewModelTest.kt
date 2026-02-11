/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.TestMonotonicFrameClock
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConverterToolTabViewModelTest {

    private lateinit var subject: ConverterToolTabViewModel

    @BeforeEach
    fun setUp() {
        subject = ConverterToolTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun scrollState() {
        assertEquals(0, subject.scrollState().value)
    }

    @Test
    fun launch() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } returns true

        subject.launch()

        verify { focusRequester.requestFocus() }
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
    @Test
    fun keyboardScrollAction() = runTest {
        val testClock = TestMonotonicFrameClock(this)
        runComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                coroutineScope.launch(testClock) {
                    subject.keyboardScrollAction(coroutineScope, Key.DirectionUp, false)
                    subject.keyboardScrollAction(coroutineScope, Key.DirectionUp, true)
                }
            }
        }
    }

}