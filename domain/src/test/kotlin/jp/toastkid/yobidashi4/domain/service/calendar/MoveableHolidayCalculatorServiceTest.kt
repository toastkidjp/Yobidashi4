/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MoveableHolidayCalculatorServiceTest {

    private lateinit var moveableHolidayCalculatorService: MoveableHolidayCalculatorService

    @BeforeEach
    fun setUp() {
        moveableHolidayCalculatorService = MoveableHolidayCalculatorService()
    }

    @Test
    fun test() {
        assertAll(
            { assertEquals(false, moveableHolidayCalculatorService.invoke(2020, 1, 12)) },
            { assertEquals(true, moveableHolidayCalculatorService.invoke(2020, 1, 13)) },
            { assertEquals(false, moveableHolidayCalculatorService.invoke(2020, 1, 14)) },
            { assertEquals(false, moveableHolidayCalculatorService.invoke(2019, 7, 14)) },
            { assertEquals(true, moveableHolidayCalculatorService.invoke(2019, 7, 15)) },
            { assertEquals(false, moveableHolidayCalculatorService.invoke(2019, 7, 16)) },
            { assertEquals(false, moveableHolidayCalculatorService.invoke(2020, 9, 20)) },
            { assertEquals(true, moveableHolidayCalculatorService.invoke(2020, 9, 21)) },
            { assertEquals(false, moveableHolidayCalculatorService.invoke(2019, 10, 13)) },
            { assertEquals(true, moveableHolidayCalculatorService.invoke(2019, 10, 14)) },
            { assertEquals(false, moveableHolidayCalculatorService.invoke(2019, 10, 15)) },
            { assertEquals(false, moveableHolidayCalculatorService.invoke(2023, 8, 14)) }
        )
    }

    @Test
    fun testFeb() {
        assertEquals(false, moveableHolidayCalculatorService.invoke(2020, 2, 1))
    }

}