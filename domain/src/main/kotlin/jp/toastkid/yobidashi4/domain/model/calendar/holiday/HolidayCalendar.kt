package jp.toastkid.yobidashi4.domain.model.calendar.holiday

import jp.toastkid.yobidashi4.domain.service.calendar.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.calendar.japan.JapaneseOffDayFinderService

enum class HolidayCalendar(
    private val offDayFinderService: OffDayFinderService,
    val flag: String = "\uD83C\uDDFA\uD83C\uDDF8"
) {

    JAPAN(JapaneseOffDayFinderService(), "\uD83C\uDDEF\uD83C\uDDF5");

    fun getHolidays(year: Int, month: Int): List<Holiday> {
        return offDayFinderService.invoke(year, month)
    }

    companion object {
        fun findByName(name: String?) = values().firstOrNull { it.name == name }
    }

}