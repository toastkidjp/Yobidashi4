/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TrimmingServiceTest {

    private lateinit var trimmingService: TrimmingService

    @BeforeEach
    fun setUp() {
        trimmingService = TrimmingService()
    }

    @Test
    fun testMultilineCase() {
        val lineSeparator = "\n"
        assertEquals(
            "john${lineSeparator}aaa${lineSeparator}trimmed",
            trimmingService.invoke(listOf("  john", " aaa   ", "trimmed  ").joinToString(lineSeparator))
        )
    }

    @ParameterizedTest
    @CsvSource(
        "'  aaa   ', aaa",
        "null, null",
        "'', ''",
        nullValues = ["null"],
    )
    fun testCases(input: String?, expected: String?) {
        assertEquals(expected, trimmingService.invoke(input))
    }

    @Test
    fun includingLineSeparatorCase() {
        assertEquals("test\ntest\n", trimmingService.invoke("test\ntest\n"))
    }

}