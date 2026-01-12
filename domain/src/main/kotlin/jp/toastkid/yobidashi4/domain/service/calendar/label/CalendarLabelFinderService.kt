/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.label

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import jp.toastkid.yobidashi4.domain.model.calendar.label.FixedCalendarLabel
import jp.toastkid.yobidashi4.domain.model.calendar.label.MoveableCalendarLabel
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarLabelFinderService {

    private fun calculateOffsetDays(
        dayOfWeek: DayOfWeek,
        candidateDayOfWeek: DayOfWeek
    ): Int {
        val offsetDays =
            if (dayOfWeek <= candidateDayOfWeek) candidateDayOfWeek.value - dayOfWeek.value + 1
            else 7 - (dayOfWeek.value - candidateDayOfWeek.value - 1)
        return offsetDays
    }

    operator fun invoke(year: Int, month: Int): MutableList<Holiday> {
        val labels = mutableListOf<Holiday>()
        labels.addAll(FixedCalendarLabel.entries.filter { it.month == month }.map { Holiday(it.title, it.month, it.date, HolidayCalendar.JAPAN.flag) })

        val localDate = LocalDate.of(year, month, 1)
        val dayOfWeek = localDate.dayOfWeek

        labels.add(Holiday("SQ", month, calculateOffsetDays(dayOfWeek, DayOfWeek.FRIDAY) + 7))
        if (month == 1) {
            labels.add(
                Holiday(
                    "大発会",
                    month,
                    when (LocalDate.of(year, month, 4).dayOfWeek) {
                        DayOfWeek.SATURDAY -> 6
                        DayOfWeek.SUNDAY -> 5
                        else -> 4
                    },
                    HolidayCalendar.JAPAN.flag
                )
            )
        }
        if (month == 12) {
            labels.add(
                Holiday(
                    "大納会",
                    month,
                    when (LocalDate.of(year, month, 30).dayOfWeek) {
                        DayOfWeek.SATURDAY -> 29
                        DayOfWeek.SUNDAY -> 28
                        else -> 30
                    },
                    HolidayCalendar.JAPAN.flag
                )
            )
        }

        MoveableCalendarLabel.entries.filter { it.month == month }.mapNotNull { it.find(year, month) }
            .forEach { labels.add(it) }

        return labels
    }

}