package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.MoveableJapaneseHoliday

class MoveableHolidayCalculatorService {

    operator fun invoke(year: Int, month: Int, date: Int): Boolean {
        if (MoveableJapaneseHoliday.isTargetMonth(month).not() && month != 8) {
            return false
        }

        return MoveableJapaneseHoliday.find(year, month).any { it.month == month && it.day == date }
    }

}