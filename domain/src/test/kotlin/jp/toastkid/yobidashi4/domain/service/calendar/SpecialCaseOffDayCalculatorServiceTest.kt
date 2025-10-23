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

}
