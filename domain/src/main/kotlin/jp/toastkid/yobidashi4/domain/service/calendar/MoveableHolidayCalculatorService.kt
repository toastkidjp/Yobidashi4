package jp.toastkid.yobidashi4.domain.service.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.model.calendar.MoveableJapaneseHoliday

class MoveableHolidayCalculatorService {

    operator fun invoke(year: Int, month: Int, date: Int): Boolean {
        if (MoveableJapaneseHoliday.isTargetMonth(month).not() && month != 8) {
            return false
        }

        val localDate = LocalDate.of(year, month, 1)
        val targetDay = MoveableJapaneseHoliday.find(month) ?: return false
        val dayOfWeek = localDate.dayOfWeek
        val d = if (dayOfWeek == DayOfWeek.MONDAY) {
            1
        } else {
            DayOfWeek.SUNDAY.value - (dayOfWeek.value - 2)
        }

        return date == d + (7 * (targetDay.week - 1))
    }

}