/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import io.mockk.MockKAnnotations
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ControlAndLeftBracketCaseTest {

    private lateinit var subject: ControlAndLeftBracketCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = ControlAndLeftBracketCase()
    }

    @AfterEach
    fun tearDown() {
    }

    @ParameterizedTest
    @CsvSource(
        "(test, 0",
        "test, 1",
        "'', 1"
    )
    fun noopCases(input: String, selectionStartIndex: Int) {
        assertFalse(subject.invoke(TextFieldState(input), selectionStartIndex))
    }

    @ParameterizedTest
    @CsvSource(
        "(test), 0, ), 5, 6",
        "(test), 5, (, 0, 1",
        "[test], 0,], 5, 6",
        "[test], 5,[, 0, 1",
        "{test}, 0,}, 5, 6",
        "{test}, 5,{, 0, 1",
        "「test」, 0,」, 5, 6",
        "「test」, 5,「, 0, 1",
        "『test』, 0, 』, 5, 6",
        "『test』, 5,『, 0, 1"
    )
    fun correctCase(
        fieldValueText: String,
        selectionStartIndex: Int,
    ) {

        val invoke = subject.invoke(TextFieldState(fieldValueText), selectionStartIndex)

        assertTrue(invoke)
    }

}