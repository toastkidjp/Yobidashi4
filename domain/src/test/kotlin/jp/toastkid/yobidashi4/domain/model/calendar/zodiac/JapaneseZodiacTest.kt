/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.zodiac

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JapaneseZodiacTest {

    private lateinit var subject: JapaneseZodiac

    @BeforeEach
    fun setUp() {
        subject = JapaneseZodiac()
    }

    @ParameterizedTest
    @CsvSource(
        "1868, 戊辰",
        "1966, 丙午",
        "2024, 甲辰",
        "2025, 乙巳",
        "2026, 丙午"
    )
    fun calculate(year: Int, expected: String) {
        assertEquals(expected, subject.calculate(year))
    }

}
