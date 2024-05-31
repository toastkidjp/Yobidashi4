package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.MoveableJapaneseHoliday

class MoveableHolidayCalculatorService {

    operator fun invoke(year: Int, month: Int, date: Int): Boolean {
        if (MoveableJapaneseHoliday.isTargetMonth(month).not()) {
            return false
        }

        return MoveableJapaneseHoliday.find(year, month).any { it.day == date }
    }

}