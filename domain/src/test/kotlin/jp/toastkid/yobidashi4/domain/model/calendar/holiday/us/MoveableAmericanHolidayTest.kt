/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.holiday.us

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

class MoveableAmericanHolidayTest {

    @Test
    fun test() {
        assertNotNull(MoveableAmericanHoliday.find(2024, 1))
        assertNotNull(MoveableAmericanHoliday.find(2023, 2))
        assertNotNull(MoveableAmericanHoliday.find(2024, 2))
        assertNotNull(MoveableAmericanHoliday.find(2020, 5))
        assertNotNull(MoveableAmericanHoliday.find(2021, 5))
        assertNotNull(MoveableAmericanHoliday.find(2022, 5))
        assertNotNull(MoveableAmericanHoliday.find(2023, 5))
        assertNotNull(MoveableAmericanHoliday.find(2024, 5))
        assertNotNull(MoveableAmericanHoliday.find(2023, 9))
        assertNotNull(MoveableAmericanHoliday.find(2025, 9))
        assertNotNull(MoveableAmericanHoliday.find(2020, 11))
        assertNotNull(MoveableAmericanHoliday.find(2023, 11))
        assertNotNull(MoveableAmericanHoliday.find(2024, 11))
    }

    @Test
    fun nullCase() {
        assertNull(MoveableAmericanHoliday.find(2025, 3))
    }

}