/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.editor.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JsonPrettyPrintTest {

    private lateinit var subject: JsonPrettyPrint

    @BeforeEach
    fun setUp() {
        subject = JsonPrettyPrint()
    }

    @Test
    fun invoke() {
        assertEquals(
            """
{
    "key": "value",
    "values": [
        "a",
        "b"
    ],
    "obj": {
        "inner": true
    }
}""".trimIndent(),
            subject.invoke("""{"key": "value", "values": ["a", "b"], "obj": { "inner": true }}""")
        )

        assertEquals("""{"incorrect json"}""", subject.invoke("""{"incorrect json"}"""))
    }

}