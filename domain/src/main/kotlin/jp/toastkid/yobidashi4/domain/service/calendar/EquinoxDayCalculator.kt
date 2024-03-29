package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday

class EquinoxDayCalculator {

    fun calculateVernalEquinoxDay(year: Int): Holiday? {
        if (year < 1980) {
            return null
        }

        val day = (20.8431 + 0.242194 * (year - 1980)).toInt() - ((year - 1980) / 4)
        if (day <= 0) {
            return null
        }

        return Holiday(
            "Vernal equinox day",
            3,
            day,
            "\uD83C\uDDEF\uD83C\uDDF5"
        )
    }

    fun calculateAutumnalEquinoxDay(year: Int): Holiday? {
        if (year < 1980) {
            return null
        }

        val day = (23.2488 + 0.242194 * (year - 1980)).toInt() - ((year - 1980) / 4)
        if (day <= 0) {
            return null
        }

        return Holiday(
            "Autumnal equinox day",
            9,
            day,
            "\uD83C\uDDEF\uD83C\uDDF5"
        )
    }

}
