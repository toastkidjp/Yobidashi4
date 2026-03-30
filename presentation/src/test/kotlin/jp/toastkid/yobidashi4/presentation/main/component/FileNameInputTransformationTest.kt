/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.text.SpanStyle
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FileNameInputTransformationTest {

    private lateinit var subject: FileNameInputTransformation

    @BeforeEach
    fun setUp() {
        subject = FileNameInputTransformation()
    }

    @Test
    fun noopCorrectName() {
        val buffer = mockk<TextFieldBuffer>()
        val original = "valid_filename_123"
        every { buffer.asCharSequence() } returns original
        every { buffer.addStyle(any<SpanStyle>(), any(), any()) } just Runs
        every { buffer.append(any<String>()) } returns buffer
        every { buffer.length } returns original.length
        every { buffer.replace(any(), any(), any()) } just Runs

        with(subject) {
            buffer.transformInput()
        }

        assertEquals(original, buffer.asCharSequence().toString())
        verify(inverse = true) { buffer.replace(any(), any(), any()) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["file/name", "test*file", "ask?me", "dir\\path", "a:b", "smaller<larger", "pipe|line", "quote\"quote"])
    fun filterIncorrectCharacter(input: String) {
        val buffer = mockk<TextFieldBuffer>()
        every { buffer.asCharSequence() } returns input
        every { buffer.addStyle(any<SpanStyle>(), any(), any()) } just Runs
        every { buffer.append(any<String>()) } returns buffer
        every { buffer.length } returns input.length
        every { buffer.replace(any(), any(), any()) } just Runs

        with(subject) {
            buffer.transformInput()
        }

        verify { buffer.replace(any(), any(), any()) }
    }

    @Test
    fun filterMultipleIncorrectCharacters() {
        val input = "my/file:name*.txt"
        val buffer = mockk<TextFieldBuffer>()
        every { buffer.asCharSequence() } returns input
        every { buffer.addStyle(any<SpanStyle>(), any(), any()) } just Runs
        every { buffer.append(any<String>()) } returns buffer
        every { buffer.length } returns input.length
        every { buffer.replace(any(), any(), any()) } just Runs

        with(subject) {
            buffer.transformInput()
        }

        verify { buffer.replace(any(), any(), any()) }
    }

}
