/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.holiday.us

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import java.time.DayOfWeek
import java.time.LocalDate

enum class MoveableAmericanHoliday(
    private val month: Int,
    val week: Int,
    val title: String,
    val dayOfWeek: DayOfWeek = DayOfWeek.MONDAY
) {

    MARTIN_LUTHER_KING_JR_DAY(1, 3,  "Martin Luther King, Jr. Day"),
    PRESIDENT_S_DAY(2, 3, "President's Day"),
    MEMORIAL_DAY(5, -1, "Memorial Day"),
    LABOR_DAY(9, 1, "Labor Day"),
    COLUMBUS_DAY(10, 2, "Columbus Day"),
    THANKSGIVING_DAY(11, 4, "Thanksgiving Day", DayOfWeek.THURSDAY)
    ;

    companion object {

        private val DAYS_OF_WEEK_FOR_LAST_WEEK =
            setOf(DayOfWeek.MONDAY, DayOfWeek.SUNDAY, DayOfWeek.SATURDAY)

        private val months = entries.map(MoveableAmericanHoliday::month).distinct()

        private fun isTargetMonth(month: Int): Boolean {
            return months.contains(month)
        }

        fun find(month: Int): MoveableAmericanHoliday? {
            return entries.firstOrNull { it.month == month }
        }

        fun find(year: Int, month: Int): Holiday? {
            if (isTargetMonth(month).not()) {
                return null
            }

            val localDate = LocalDate.of(year, month, 1)
            val candidate = find(month) ?: return null
            val dayOfWeek = localDate.dayOfWeek
            val offsetDays =
                if (dayOfWeek <= candidate.dayOfWeek) candidate.dayOfWeek.value - dayOfWeek.value + 1
                else 7 - (dayOfWeek.value - candidate.dayOfWeek.value - 1)

            val targetWeek = when {
                (candidate.week != -1) -> candidate.week
                DAYS_OF_WEEK_FOR_LAST_WEEK.contains(dayOfWeek) -> 5
                else -> 4
            }

            return Holiday(candidate.title, candidate.month, offsetDays + (7 * (targetWeek - 1)))
        }

    }

}