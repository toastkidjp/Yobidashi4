/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.japan

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JapaneseOffDayFinderServiceTest {

    private lateinit var japaneseOffDayFinderService: JapaneseOffDayFinderService

    @BeforeEach
    fun setUp() {
        japaneseOffDayFinderService = JapaneseOffDayFinderService()
    }

    @ParameterizedTest
    @CsvSource(
        "2016, 11",
        "2020, 10",
        "2021, 9",
        "2022, 11"
    )
    fun mountainDay(year: Int, expectedDate: Int) {
        assertEquals(expectedDate, japaneseOffDayFinderService.invoke(year, 8).firstOrNull()?.day)
    }

    @Test
    fun mountainDayBefore2016() {
        assertTrue(japaneseOffDayFinderService.invoke(2015, 8).isEmpty())
    }

    @ParameterizedTest
    @CsvSource(
        "2013",
        "2014",
        "2015"
    )
    fun substituteHolidayInMay(year: Int) {
        assertNotNull(japaneseOffDayFinderService.invoke(year, 5).firstOrNull { it.day == 6 })
    }

    @Test
    fun noneSubstituteHolidayInMayCase() {
        assertNull(japaneseOffDayFinderService.invoke(2016, 5).firstOrNull { it.day == 6 })
    }

    @Test
    fun march() {
        assertEquals(20, japaneseOffDayFinderService.invoke(2024, 3).first().day)
    }

    @Test
    fun september() {
        assertEquals(23, japaneseOffDayFinderService.invoke(2024, 9).first().day)
    }

    @ParameterizedTest
    @CsvSource(
        "224, 9, 1",
        "20124, 4, 1",
    )
    fun checkIrregularYearInputCases(year: Int, month: Int, expected: Int) {
        assertTrue(japaneseOffDayFinderService.invoke(year, 3, false).isEmpty())
        assertEquals(expected, japaneseOffDayFinderService.invoke(224, month, false).size)
    }

    @ParameterizedTest
    @CsvSource(
        "1006, 1",
        "2006, 2",
        "2009, 3",
        "2015, 3",
        "2026, 3",
        "2020, 2",
        "2021, 2",
        "2025, 2",
    )
    fun septemberSubstitute(year: Int, expectedDate: Int) {
        assertEquals(expectedDate, japaneseOffDayFinderService.invoke(year, 9).size)
    }

}