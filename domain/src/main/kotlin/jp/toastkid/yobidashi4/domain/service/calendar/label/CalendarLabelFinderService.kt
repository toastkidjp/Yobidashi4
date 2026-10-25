/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.label

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Label
import jp.toastkid.yobidashi4.domain.model.calendar.label.FixedCalendarLabel
import jp.toastkid.yobidashi4.domain.model.calendar.label.MoveableCalendarLabel
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarLabelFinderService {

    private class ToshoCeremony(
        private val saturdayCaseDate: Int,
        private val sundayCaseDate: Int,
        private val otherCaseDate: Int
    ) {
        fun calculateDate(dayOfWeek: DayOfWeek): Int {
            return when (dayOfWeek) {
                DayOfWeek.SATURDAY -> saturdayCaseDate
                DayOfWeek.SUNDAY -> sundayCaseDate
                else -> otherCaseDate
            }
        }
    }

    private val DAI_HAKKAI = ToshoCeremony(6,5,4)

    private val DAI_NOKAI = ToshoCeremony(29,28,30)

    private fun calculateOffsetDays(dayOfWeek: DayOfWeek): Int {
        val offsetDays =
            if (dayOfWeek <= DayOfWeek.FRIDAY) DayOfWeek.FRIDAY.value - dayOfWeek.value + 1
            else 7 - (dayOfWeek.value - DayOfWeek.FRIDAY.value - 1)
        return offsetDays
    }

    operator fun invoke(year: Int, month: Int): MutableList<Label> {
        val labels = mutableListOf<Label>()
        labels.addAll(
            FixedCalendarLabel.entries
                .filter { it.month == month }
                .map { Label(it.title, it.month, it.date, HolidayCalendar.JAPAN.flag) }
        )

        val localDate = LocalDate.of(year, month, 1)
        val dayOfWeek = localDate.dayOfWeek

        labels.add(
            Label("SQ", month, calculateOffsetDays(dayOfWeek) + 7, HolidayCalendar.JAPAN.flag)
        )
        if (month == 1) {
            labels.add(
                Label(
                    "大発会",
                    month,
                    DAI_HAKKAI.calculateDate(LocalDate.of(year, month, 4).dayOfWeek),
                    HolidayCalendar.JAPAN.flag
                )
            )
        }
        if (month == 12) {
            labels.add(
                Label(
                    "大納会",
                    month,
                    DAI_NOKAI.calculateDate(LocalDate.of(year, month, 30).dayOfWeek),
                    HolidayCalendar.JAPAN.flag
                )
            )
        }

        MoveableCalendarLabel.entries
            .filter { it.month == month }
            .mapNotNull { it.find(year, month) }
            .forEach(labels::add)

        return labels
    }

}