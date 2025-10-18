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
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class MoveableHolidayCalculatorServiceTest {

    private lateinit var moveableHolidayCalculatorService: MoveableHolidayCalculatorService

    @BeforeEach
    fun setUp() {
        moveableHolidayCalculatorService = MoveableHolidayCalculatorService()
    }

    @ParameterizedTest
    @CsvSource(
        "2020, 1, 12, false",
        "2020, 1, 13, true",
        "2020, 1, 14, false",
        "2019, 7, 14, false",
        "2019, 7, 15, true",
        "2019, 7, 16, false",
        "2020, 9, 20, false",
        "2020, 9, 21, true",
        "2019, 10, 13, false",
        "2019, 10, 14, true",
        "2019, 10, 15, false",
        "2023, 8, 14, false"
    )
    fun test(year: Int, month: Int, date: Int, expected: Boolean) {
        assertEquals(expected, moveableHolidayCalculatorService.invoke(year, month, date))
    }

    @Test
    fun testFeb() {
        assertEquals(false, moveableHolidayCalculatorService.invoke(2020, 2, 1))
    }

}