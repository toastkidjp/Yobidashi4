/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class SpecialCaseOffDayCalculatorServiceTest {

    private lateinit var specialCaseOffDayCalculatorService: SpecialCaseOffDayCalculatorService

    @BeforeEach
    fun setUp() {
        specialCaseOffDayCalculatorService = SpecialCaseOffDayCalculatorService()
    }

    @ParameterizedTest
    @CsvSource(
        "2019, 1, 0",
        "2019, 4, 1",
        "2019, 5, 2",
        "2019, 7, 0",
        "2020, 7, 2",
        "2021, 7, 2",
        "2020, 8, 0",
        "2021, 8, 0",
        "2020, 10, 0",
        "2021, 10, 0",
        "2021, 4, 0",
    )
    fun test(year: Int, month: Int, expected: Int) {
        assertEquals(expected, specialCaseOffDayCalculatorService.invoke(year, month).size)
    }

    @Test
    fun testNormalDay() {
        val result = specialCaseOffDayCalculatorService.invoke(2019, 1)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2019_4() {
        val result = specialCaseOffDayCalculatorService.invoke(2019, 4)

        assertEquals(1, result.size)
    }

    @Test
    fun test2019_May() {
        val result = specialCaseOffDayCalculatorService.invoke(2019, 5)

        assertEquals(2, result.size)
    }

    @Test
    fun test2019_July() {
        val result = specialCaseOffDayCalculatorService.invoke(2019, 7)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2020_July() {
        val result = specialCaseOffDayCalculatorService.invoke(2020, 7)

        assertEquals(2, result.size)
    }

    @Test
    fun test2021_July() {
        val result = specialCaseOffDayCalculatorService.invoke(2021, 7)

        assertEquals(2, result.size)
    }

    @Test
    fun test2020_August() {
        val result = specialCaseOffDayCalculatorService.invoke(2020, 8)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2020_Oct() {
        val result = specialCaseOffDayCalculatorService.invoke(2020, 10)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2021_August() {
        val result = specialCaseOffDayCalculatorService.invoke(2021, 8)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2021_Oct() {
        val result = specialCaseOffDayCalculatorService.invoke(2021, 10)

        assertTrue(result.isEmpty())
    }

    @Test
    fun test2021_Apr() {
        val result = specialCaseOffDayCalculatorService.invoke(2021, 4)

        assertTrue(result.isEmpty())
    }

}