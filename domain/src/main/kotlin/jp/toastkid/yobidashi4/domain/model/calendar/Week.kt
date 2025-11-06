/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import java.time.DayOfWeek
import java.time.LocalDate

class Week {

    private val days: MutableList<CalendarDate> = mutableListOf()

    fun add(date: LocalDate, holidays: List<Holiday> = emptyList()) {
        days.add(
            CalendarDate(
                date.dayOfMonth,
                date.dayOfWeek,
                holidays.map { it.flag + " " + it.title },
                offDay = holidays.any { it.flag == HolidayCalendar.JAPAN.flag }
            )
        )
    }

    fun addEmpty() {
        days.add(CalendarDate(-1, DayOfWeek.MONDAY))
    }

    fun days() = days

    fun anyApplicableDate() = days.any { it.date != -1 }

}
