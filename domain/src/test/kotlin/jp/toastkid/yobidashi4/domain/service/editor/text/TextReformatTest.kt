/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.editor.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextReformatTest {

    private lateinit var subject: TextReformat

    @BeforeEach
    fun setUp() {
        subject = TextReformat()
    }

    @Test
    fun invoke() {
        assertTrue(subject.invoke("").isEmpty())

        assertEquals("text", subject.invoke("  text"))

        assertEquals(
            """
a
  b
    c""",
            subject.invoke(
                """
            a
              b
                c"""
            )
        )
    }

}
