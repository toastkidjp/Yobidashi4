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
import androidx.compose.ui.input.pointer.PointerInputChange
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PointerEventAdapterTest {

    private lateinit var subject: PointerEventAdapter

    @MockK
    private lateinit var event: PointerEvent

    @OptIn(ExperimentalComposeUiApi::class)
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        val pointerInputChange = makePointerInputChange(true)
        event = spyk(PointerEvent(listOf(pointerInputChange)))
        every { event.button } returns PointerButton.Secondary

        subject = PointerEventAdapter()
    }

    private fun makePointerInputChange(pressed: Boolean): PointerInputChange {
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns pressed
        return pointerInputChange
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun isSecondaryClick() {
        assertTrue(subject.isSecondaryClick(event))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun isNotSecondaryClick() {
        every { event.button } returns PointerButton.Primary

        assertFalse(subject.isSecondaryClick(event))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun isNotClick() {
        val pointerInputChange = makePointerInputChange(false)
        event = spyk(PointerEvent(listOf(pointerInputChange)))

        assertFalse(subject.isSecondaryClick(event))
    }

}
