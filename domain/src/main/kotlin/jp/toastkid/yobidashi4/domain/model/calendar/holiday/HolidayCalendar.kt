/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.holiday

import jp.toastkid.yobidashi4.domain.service.calendar.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.calendar.japan.JapaneseOffDayFinderService
import jp.toastkid.yobidashi4.domain.service.calendar.uk.UKOffDayFinder
import jp.toastkid.yobidashi4.domain.service.calendar.us.AmericanOffDayFinder

enum class HolidayCalendar(
    private val offDayFinderService: OffDayFinderService,
    val flag: String = "\uD83C\uDDFA\uD83C\uDDF8"
) {

    JAPAN(JapaneseOffDayFinderService(), "\uD83C\uDDEF\uD83C\uDDF5"),
    UK(UKOffDayFinder(), "\uD83C\uDDEC\uD83C\uDDE7"),
    US(AmericanOffDayFinder(), "\uD83C\uDDFA\uD83C\uDDF8");

    fun getHolidays(year: Int, month: Int): List<Holiday> {
        return offDayFinderService.invoke(year, month)
    }

    companion object {
        fun findByName(name: String?) = entries.firstOrNull { it.name == name }
    }

}