package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.MoveableJapaneseHoliday

class MoveableHolidayCalculatorService {

    operator fun invoke(year: Int, month: Int, date: Int): Boolean {
        if (MoveableJapaneseHoliday.isTargetMonth(month).not() && month != 8) {
            return false
        }

        val targetDay = MoveableJapaneseHoliday.find(year, month).first()
        return targetDay.month == month && targetDay.day == date
    }

}