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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MoveableAmericanHolidayTest {

    @ParameterizedTest
    @CsvSource(
        "2024, 1",
        "2023, 2",
        "2024, 2",
        "2020, 5",
        "2021, 5",
        "2022, 5",
        "2023, 5",
        "2024, 5",
        "2023, 9",
        "2025, 9",
        "2020, 11",
        "2023, 11",
        "2024, 11"
    )
    fun test(year: Int, month: Int) {
        assertNotNull(MoveableAmericanHoliday.find(year, month))
    }

    @Test
    fun nullCase() {
        assertNull(MoveableAmericanHoliday.find(2025, 3))
    }

}