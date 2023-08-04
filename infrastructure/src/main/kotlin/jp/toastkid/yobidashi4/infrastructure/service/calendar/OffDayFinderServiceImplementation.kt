package jp.toastkid.yobidashi4.infrastructure.service.calendar

import java.time.DayOfWeek
import jp.toastkid.yobidashi4.domain.model.calendar.FixedJapaneseHoliday
import jp.toastkid.yobidashi4.domain.service.article.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.calendar.EquinoxDayCalculator
import jp.toastkid.yobidashi4.domain.service.calendar.MoveableHolidayCalculatorService
import jp.toastkid.yobidashi4.domain.service.calendar.SpecialCaseOffDayCalculatorService
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class OffDayFinderServiceImplementation(
    private val equinoxDayCalculator: EquinoxDayCalculator = EquinoxDayCalculator(),
    private val moveableHolidayCalculatorService: MoveableHolidayCalculatorService = MoveableHolidayCalculatorService(),
    private val specialCaseOffDayCalculator: SpecialCaseOffDayCalculatorService = SpecialCaseOffDayCalculatorService()
) : OffDayFinderService, KoinComponent {

    private val userOffDayService: UserOffDayService by inject()

    override operator fun invoke(year: Int, month: Int, date: Int, dayOfWeek: DayOfWeek, useUserOffDay: Boolean): Boolean {
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

        if (useUserOffDay && userOffDayService(month, date)) {
            return true
        }

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
