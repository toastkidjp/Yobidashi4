/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.holiday.us

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MoveableAmericanHolidayTest {

    @Test
    fun test() {
        assertTrue(MoveableAmericanHoliday.isHoliday(2024, 1))
        assertTrue(MoveableAmericanHoliday.isHoliday(2023, 2))
        assertTrue(MoveableAmericanHoliday.isHoliday(2024, 2))
        assertTrue(MoveableAmericanHoliday.isHoliday(2020, 5))
        assertTrue(MoveableAmericanHoliday.isHoliday(2021, 5))
        assertTrue(MoveableAmericanHoliday.isHoliday(2022, 5))
        assertTrue(MoveableAmericanHoliday.isHoliday(2023, 5))
        assertTrue(MoveableAmericanHoliday.isHoliday(2024, 5))
        assertTrue(MoveableAmericanHoliday.isHoliday(2023, 9))
        assertTrue(MoveableAmericanHoliday.isHoliday(2025, 9))
        assertTrue(MoveableAmericanHoliday.isHoliday(2020, 11))
        assertTrue(MoveableAmericanHoliday.isHoliday(2023, 11))
        assertTrue(MoveableAmericanHoliday.isHoliday(2024, 11))
    }

}