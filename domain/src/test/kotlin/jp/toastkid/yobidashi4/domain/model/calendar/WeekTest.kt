/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Label
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class WeekTest {

    private val week: Week = Week()

    @Test
    fun test() {
        week.add(LocalDate.of(2023, 8, 6))
        week.add(LocalDate.of(2023, 8, 7))
        week.add(LocalDate.of(2023, 8, 8))
        week.add(LocalDate.of(2023, 8, 9))
        week.add(LocalDate.of(2023, 8, 10))
        week.add(LocalDate.of(2023, 8, 11), emptyList())
        week.add(LocalDate.of(2023, 8, 11), listOf(Holiday("Mountain day", 8, 11)))
        week.add(LocalDate.of(2023, 8, 13), emptyList(), listOf(Label("test", 1, 1)))
        week.addEmpty()

        assertEquals(9, week.days().size)
        assertTrue(week.anyApplicableDate())
    }

}