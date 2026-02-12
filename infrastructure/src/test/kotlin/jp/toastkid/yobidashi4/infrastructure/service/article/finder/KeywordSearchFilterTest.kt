/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class KeywordSearchFilterTest {

    @ParameterizedTest
    @CsvSource(
        "This text exists for test., true",
        "これはtestです., true",
        "これはtetです., true",
        "'', true",
        "null, false",
        nullValues = ["null"]
    )
    fun invoke(input: String?, expected: Boolean) {
        val keywordSearchFilter = KeywordSearchFilter(null)
        assertEquals(expected, keywordSearchFilter.invoke(input))
    }

    @ParameterizedTest
    @CsvSource(
        "This text exists for test., true",
        "これはtestです., true",
        "これはtetです., false",
        "'', false",
        "null, false",
        nullValues = ["null"]
    )
    fun invokeWithSingleConstructorParameter(input: String?, expected: Boolean) {
        val keywordSearchFilter = KeywordSearchFilter("test")
        assertEquals(expected, keywordSearchFilter.invoke(input))
    }

    @ParameterizedTest
    @CsvSource(
        "This text exists for test., true",
        "これはtestです., true",
        "これはtetです., true",
        "'', false",
        "null, false",
        nullValues = ["null"]
    )
    fun invokeWithMultipleConstructorParameter(input: String?, expected: Boolean) {
        val keywordSearchFilter = KeywordSearchFilter("test これ")
        assertEquals(expected, keywordSearchFilter.invoke(input))
    }

    @ParameterizedTest
    @CsvSource(
        "This text exists for test., false",
        "これはtestです., true",
        "これはtetです., false",
        "'', false",
        "null, false",
        nullValues = ["null"]
    )
    fun invokeWithMultipleConstructorParameterForExactMatch(input: String?, expected: Boolean) {
        val keywordSearchFilter = KeywordSearchFilter("test*これ*です")
        assertEquals(expected, keywordSearchFilter.invoke(input))
    }

    @ParameterizedTest
    @CsvSource(
        "This text exists for test., true",
        "これはtestです., false",
        "これはtetです., false",
        "'', false",
        "null, false",
        nullValues = ["null"]
    )
    fun invokeForExactMatch(input: String?, expected: Boolean) {
        val keywordSearchFilter = KeywordSearchFilter("\"for test\"")
        assertEquals(expected, keywordSearchFilter.invoke(input))
    }

    @Test
    fun init() {
        assertFalse(KeywordSearchFilter("input ").invoke("test"))
    }

}