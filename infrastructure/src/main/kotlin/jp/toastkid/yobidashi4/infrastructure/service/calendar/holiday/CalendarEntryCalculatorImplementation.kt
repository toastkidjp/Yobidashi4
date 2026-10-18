/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.calendar.holiday

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.CalendarEntry
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import jp.toastkid.yobidashi4.domain.service.calendar.holiday.CalendarEntryCalculator
import jp.toastkid.yobidashi4.domain.service.calendar.label.CalendarLabelFinderService
import org.koin.core.annotation.Single

@Single
class CalendarEntryCalculatorImplementation(
    private val calendarLabelFinderService: CalendarLabelFinderService = CalendarLabelFinderService(),
    private val userOffDayService: UserOffDayService
) : CalendarEntryCalculator {

    private val monthCache = mutableMapOf<String, Set<CalendarEntry>>()

    private val dateCache = mutableMapOf<String, List<CalendarEntry>>()

    override operator fun invoke(year: Int, month: Int, dayOfMonth: Int): List<CalendarEntry> {
        val key = "${year}_${month}_$dayOfMonth"
        val cached = dateCache.get(key)
        if (cached != null) {
            return cached
        }

        val candidates = findByYearMonth(year, month)
        val holidays = candidates.filter { it.dayOfMonth() == dayOfMonth }

        dateCache.put(key, holidays)

        return holidays
    }

    private fun findByYearMonth(year: Int, month: Int): Set<CalendarEntry> {
        val key = "${year}_$month"
        val cached = monthCache.get(key)
        if (cached != null) {
            return cached
        }

        val calendarEntries = HolidayCalendar.JAPAN
            .getHolidays(year, month)
            .union(userOffDayService.findBy(month))
            .union(HolidayCalendar.UK.getHolidays(year, month))
            .union(HolidayCalendar.US.getHolidays(year, month))
            .union(calendarLabelFinderService.invoke(year, month))

        monthCache.put(key, calendarEntries)

        return calendarEntries
    }

}