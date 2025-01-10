package jp.toastkid.yobidashi4.domain.service.article

import jp.toastkid.yobidashi4.domain.service.calendar.japan.JapaneseOffDayFinderService
import java.time.Month

class JapaneseStockMarketCloseDaysFinder {

    operator fun invoke(year: Int, month: Month, day: Int): Boolean {
        return when (month) {
            Month.DECEMBER -> {
                return day == 31
            }
            Month.JANUARY -> {
                return day <= 3
            }
            else -> JapaneseOffDayFinderService().invoke(year, month.value).any { it.day == day }
        }
    }

}