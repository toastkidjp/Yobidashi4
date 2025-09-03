/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.uk

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday

class EasterHolidayCalculator {

    operator fun invoke(year: Int, month: Int): List<Holiday> {
        val (march, _) = calculateDate(year)
        val goodFridayDate = march - 2
        val easterMondayDate = march + 1
        return listOf(
            Holiday(
                "Good Friday",
                if (inMarch(goodFridayDate)) 3 else 4,
                if (inMarch(goodFridayDate)) goodFridayDate else goodFridayDate - 31,
                "\uD83C\uDDEC\uD83C\uDDE7"
            ),
            Holiday(
                "Easter Monday",
                if (inMarch(easterMondayDate)) 3 else 4,
                if (inMarch(easterMondayDate)) easterMondayDate else easterMondayDate - 31,
                "\uD83C\uDDEC\uD83C\uDDE7"
            )
        ).filter { it.month == month }
    }

    private fun inMarch(march: Int) = march in 1..31

    private fun calculateDate(year: Int): Pair<Int, Int> {
        val a = year.mod(19)
        val b = year.mod(4)
        val c = year.mod(7)
        val k = year.div(100)
        val p = (13 + 8 * k).div(25)
        val q = (k.div(4))
        val m = (15 - p + k - q).mod(30)
        val n = (4 + k - q).mod(7)
        val d = (19*a + m).mod(30)
        val e = (2*b + 4 * c + 6 * d + n).mod(7)
        val march = 22 + d + e
        val april = d + e - 9
        return march to april
    }

}
