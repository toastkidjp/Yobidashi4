/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.uk

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.uk.FixedUKHoliday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.uk.MoveableUKHoliday
import jp.toastkid.yobidashi4.domain.service.calendar.OffDayFinderService
import java.time.DayOfWeek
import java.time.LocalDate

class UKOffDayFinder(
    private val easterHolidayCalculator: EasterHolidayCalculator = EasterHolidayCalculator()
) : OffDayFinderService {

    override fun invoke(year: Int, month: Int, useUserOffDay: Boolean): List<Holiday> {
        val holidays = mutableListOf<Holiday>()

        val firstOrNull = FixedUKHoliday.find(month)
        holidays.addAll(firstOrNull)

        val substitutes = holidays.mapNotNull {
            val date = LocalDate.of(year, month, it.day)
            if (date.dayOfWeek == DayOfWeek.SUNDAY) {
                Holiday("Substitute Holiday", month, it.day + 1, HolidayCalendar.UK.flag)
            } else null
        }

        holidays.addAll(MoveableUKHoliday.find(year, month))
        holidays.addAll(easterHolidayCalculator.invoke(year, month))

        return substitutes.union(holidays).toList()
    }

}