/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.us

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.us.FixedAmericanHoliday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.us.MoveableAmericanHoliday
import jp.toastkid.yobidashi4.domain.service.calendar.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.calendar.uk.EasterHolidayCalculator
import java.time.DayOfWeek
import java.time.LocalDate

class AmericanOffDayFinder(
    private val easterHolidayCalculator: EasterHolidayCalculator = EasterHolidayCalculator()
) : OffDayFinderService {

    override fun invoke(year: Int, month: Int, useUserOffDay: Boolean): List<Holiday> {
        val holidays = mutableListOf<Holiday>()

        val firstOrNull = FixedAmericanHoliday.find(year, month)
        if (firstOrNull != null) {
            holidays.add(firstOrNull)
        }

        val substitutes = holidays.mapNotNull {
            val date = LocalDate.of(year, month, it.day)
            if (month != 5 && date.dayOfWeek == DayOfWeek.SUNDAY) {
                Holiday("Substitute Holiday", month, it.day + 1, HolidayCalendar.US.flag)
            } else null
        }

        val moveable = MoveableAmericanHoliday.find(year, month)
        if (moveable != null) {
            holidays.add(moveable)
        }

        return substitutes.union(holidays).toList()
    }

}