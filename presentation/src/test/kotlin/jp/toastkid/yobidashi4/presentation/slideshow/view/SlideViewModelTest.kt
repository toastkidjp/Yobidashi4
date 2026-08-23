/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

class SlideViewModelTest {

    private lateinit var subject: SlideViewModel

    @BeforeEach
    fun setUp() {
        subject = SlideViewModel()
    }

    @Test
    fun scrollState() {
        assertEquals(0, subject.scrollState().value)
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun requestFocus() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } returns true

        subject.requestFocus()

        verify { focusRequester.requestFocus() }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun keyboardScrollAction() {
        subject.keyboardScrollAction(KeyEvent(Key.DirectionUp, KeyEventType.KeyUp))
        subject.keyboardScrollAction(KeyEvent(Key.DirectionDown, KeyEventType.KeyDown))
    }

}