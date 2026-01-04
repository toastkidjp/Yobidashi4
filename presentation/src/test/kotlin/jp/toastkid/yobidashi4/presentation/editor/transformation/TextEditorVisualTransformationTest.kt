/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.transformation

import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.SpanStyle
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextEditorVisualTransformationTest {

    private lateinit var subject: TextEditorOutputTransformation

    @BeforeEach
    fun setUp() {
        subject = TextEditorOutputTransformation(TextFieldState(), true)
    }

    @Test
    fun visualTransformation() {
        val buffer = mockk<TextFieldBuffer>()
        every { buffer.asCharSequence() } returns "# Test doc"
        every { buffer.addStyle(any<SpanStyle>(), any(), any()) } just Runs
        every { buffer.append(any<String>()) } returns buffer

        with(subject) {
            buffer.transformOutput()
            buffer.transformOutput()
        }

        verify { buffer.addStyle(any<SpanStyle>(), any(), any()) }
        verify { buffer.append("[EOF]") }
    }

}