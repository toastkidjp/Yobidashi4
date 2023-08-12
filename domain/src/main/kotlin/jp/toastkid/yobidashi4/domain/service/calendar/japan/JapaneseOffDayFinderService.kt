package jp.toastkid.yobidashi4.domain.service.calendar.japan

import java.util.Calendar
import java.util.GregorianCalendar
import jp.toastkid.yobidashi4.domain.model.calendar.FixedJapaneseHoliday
import jp.toastkid.yobidashi4.domain.model.calendar.MoveableJapaneseHoliday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.service.calendar.EquinoxDayCalculator
import jp.toastkid.yobidashi4.domain.service.calendar.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.calendar.SpecialCaseOffDayCalculatorService

class JapaneseOffDayFinderService(
    private val equinoxDayCalculator: EquinoxDayCalculator = EquinoxDayCalculator(),
    private val specialCaseOffDayCalculator: SpecialCaseOffDayCalculatorService = SpecialCaseOffDayCalculatorService()
) : OffDayFinderService {

    override operator fun invoke(year: Int, month: Int, useUserOffDay: Boolean): List<Holiday> {
        if (month == 6) {
            return emptyList()
        }

        val holidays = mutableListOf<Holiday>()

        if (month == 3) {
            val vernalEquinoxDay = equinoxDayCalculator.calculateVernalEquinoxDay(year)
            holidays.add(vernalEquinoxDay)
        }

        if (month == 9) {
            val autumnalEquinoxDay = equinoxDayCalculator.calculateAutumnalEquinoxDay(year)
            holidays.add(autumnalEquinoxDay)
        }

        /*if (useUserOffDay && userOffDayService(month, date)) {
            return true
        }*/

        val firstOrNull = FixedJapaneseHoliday.find(year, month)
        holidays.addAll(firstOrNull)

        if (month == 5) {
            val calendar = GregorianCalendar(year, month - 1, 6)
            if (calendar.get(Calendar.DAY_OF_WEEK) <= Calendar.WEDNESDAY) {
                holidays.add(Holiday("Substitute holiday", month, 6, "\uD83C\uDDEF\uD83C\uDDF5"))
            }
        }

        val substitutes = holidays.mapNotNull {
            val calendar = GregorianCalendar(year, month - 1, it.day)
            if (month != 5 && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                Holiday("Substitute Holiday", month, it.day + 1, "\uD83C\uDDEF\uD83C\uDDF5")
            } else null
        }

        holidays.addAll(specialCaseOffDayCalculator(year, month))
        holidays.addAll(MoveableJapaneseHoliday.find(year, month))

        return substitutes.union(holidays).toList()
    }

}