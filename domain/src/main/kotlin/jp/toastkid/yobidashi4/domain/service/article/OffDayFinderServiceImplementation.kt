package jp.toastkid.yobidashi4.domain.service.article

import java.time.DayOfWeek
import jp.toastkid.yobidashi4.domain.model.calendar.FixedJapaneseHoliday
import jp.toastkid.yobidashi4.domain.service.calendar.EquinoxDayCalculator
import jp.toastkid.yobidashi4.domain.service.calendar.MoveableHolidayCalculatorService
import jp.toastkid.yobidashi4.domain.service.calendar.SpecialCaseOffDayCalculatorService

class OffDayFinderServiceImplementation(
    private val equinoxDayCalculator: EquinoxDayCalculator = EquinoxDayCalculator(),
    private val moveableHolidayCalculatorService: MoveableHolidayCalculatorService = MoveableHolidayCalculatorService(),
    private val specialCaseOffDayCalculator: SpecialCaseOffDayCalculatorService = SpecialCaseOffDayCalculatorService()
) {

    operator fun invoke(year: Int, month: Int, date: Int, dayOfWeek: DayOfWeek, useUserOffDay: Boolean = true): Boolean {
        if (month == 6) {
            return false
        }

        if (month == 3 && date == equinoxDayCalculator.calculateVernalEquinoxDay(year)) {
            return true
        }

        if (month == 9 && date == equinoxDayCalculator.calculateAutumnalEquinoxDay(year)) {
            return true
        }

        val isSpecialCase = specialCaseOffDayCalculator(year, month, date)
        if (isSpecialCase.first) {
            return true
        }
        if (isSpecialCase.second) {
            return false
        }

        if (moveableHolidayCalculatorService(year, month, date)) {
            return true
        }

        /*TODO if (useUserOffDay && userOffDayService(month, date)) {
            return true
        }*/

        var firstOrNull = FixedJapaneseHoliday.values()
            .firstOrNull { month == it.month && date == it.date }
        if (firstOrNull == null) {
            if (month == 5 && date == 6 && dayOfWeek <= DayOfWeek.WEDNESDAY) {
                return true
            }
            if (dayOfWeek == DayOfWeek.MONDAY) {
                firstOrNull = FixedJapaneseHoliday.values()
                    .firstOrNull { month == it.month && (date - 1) == it.date }
            }
        }
        return firstOrNull != null
    }

}
