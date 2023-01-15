package jp.toastkid.yobidashi4.domain.service.calendar

class EquinoxDayCalculator {

    fun calculateVernalEquinoxDay(year: Int): Int {
        return (20.8431 + 0.242194 * (year - 1980)).toInt() - ((year - 1980) / 4)
    }

    fun calculateAutumnalEquinoxDay(year: Int): Int {
        return (23.2488 + 0.242194 * (year - 1980)).toInt() - ((year - 1980) / 4)
    }

}
