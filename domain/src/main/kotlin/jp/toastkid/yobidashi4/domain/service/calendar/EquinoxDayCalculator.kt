package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday

class EquinoxDayCalculator {

    fun calculateVernalEquinoxDay(year: Int): Holiday {
        return Holiday(
            "Vernal equinox day",
            3,
            (20.8431 + 0.242194 * (year - 1980)).toInt() - ((year - 1980) / 4),
            "\uD83C\uDDEF\uD83C\uDDF5"
        )
    }

    fun calculateAutumnalEquinoxDay(year: Int): Holiday {
        return Holiday(
            "Autumnal equinox day",
            9,
            (23.2488 + 0.242194 * (year - 1980)).toInt() - ((year - 1980) / 4),
            "\uD83C\uDDEF\uD83C\uDDF5"
        )
    }

}
