/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.holiday.uk

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import jp.toastkid.yobidashi4.domain.service.calendar.MondayDateCalculator

enum class MoveableUKHoliday(private val month: Int, val week: Int, val title: String) {
    EARLY_MAY_BANK_HOLIDAY(5, 1, "Early May Bank Holiday"),
    SPRING_BANK_HOLIDAY(5, -1, "Spring Bank Holiday"),
    SUMMER_BANK_HOLIDAY(8, -1, "Summer Bank Holiday");

    companion object {

        private val dateCalculator = MondayDateCalculator()

        fun find(year: Int, month: Int): List<Holiday> = entries
            .filter { it.month == month }
            .map {
                Holiday(
                    it.title,
                    month,
                    dateCalculator.invoke(year, month, it.week),
                    HolidayCalendar.UK.flag
                )
            }

    }
}