/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.text.code

import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.text.SpanStyle
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CodeBlockViewOutputTransformationTest {

    private lateinit var subject: CodeBlockViewOutputTransformation

    @BeforeEach
    fun setUp() {
        subject = CodeBlockViewOutputTransformation()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun invoke() {
        val code = """
public class Code {
                
    public static void main(final String[] args) {
        System.out.println("test 001.");
    }

}
            """

        val textBuffer = mockk<TextFieldBuffer>()
        every { textBuffer.addStyle(any<SpanStyle>(), any(), any()) } just Runs
        every { textBuffer.asCharSequence() } returns code
        with(subject) {
            textBuffer.transformOutput()
        }

        verify { textBuffer.addStyle(any<SpanStyle>(), any(), any()) }
    }

}