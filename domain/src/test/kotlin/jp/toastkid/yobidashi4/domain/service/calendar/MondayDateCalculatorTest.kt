/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MondayDateCalculatorTest {

    private lateinit var subject: MondayDateCalculator

    @BeforeEach
    fun setUp() {
        subject = MondayDateCalculator()
    }

    @ParameterizedTest
    @CsvSource(
        "2025, 5, 1, 5",
        "2025, 5, -1, 26",
        "2022, 8, -1, 29",
        "2025, 9, 2, 8",
        "2025, 9, 3, 15",
        "2025, 11, 4, 24",
    )
    fun test(year: Int, month: Int, date: Int, expected: Int) {
        assertEquals(expected, subject.invoke(year, month, date))
    }

}
