/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.label

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import java.time.DayOfWeek
import java.time.LocalDate

enum class MoveableCalendarLabel(
    val month: Int,
    val week: Int,
    val dayOfWeek: DayOfWeek,
    val title: String
) {
    START_DAY_OF_DAYLIGHT_SAVING_TIME(3, 1, DayOfWeek.SUNDAY, "夏時間入り"),
    END_DAY_OF_DAYLIGHT_SAVING_TIME(11, 1, DayOfWeek.SUNDAY, "冬時間入り");

    private val DAYS_OF_WEEK_FOR_LAST_WEEK =
        setOf(DayOfWeek.MONDAY, DayOfWeek.SUNDAY, DayOfWeek.SATURDAY)

    private fun findCandidate(month: Int): MoveableCalendarLabel? {
        return MoveableCalendarLabel.entries.firstOrNull { it.month == month }
    }

    private fun calculateOffsetDays(
        dayOfWeek: DayOfWeek,
        candidateDayOfWeek: DayOfWeek
    ): Int {
        val offsetDays =
            if (dayOfWeek <= candidateDayOfWeek) candidateDayOfWeek.value - dayOfWeek.value + 1
            else 7 - (dayOfWeek.value - candidateDayOfWeek.value - 1)
        return offsetDays
    }

    fun find(year: Int, month: Int): Holiday? {
        val candidate = findCandidate(month) ?: return null
        val localDate = LocalDate.of(year, month, 1)
        val dayOfWeek = localDate.dayOfWeek
        val offsetDays = calculateOffsetDays(dayOfWeek, candidate.dayOfWeek)

        val targetWeek = candidate.week

        return Holiday(candidate.title, candidate.month, offsetDays + (7 * (targetWeek - 1)))
    }

}