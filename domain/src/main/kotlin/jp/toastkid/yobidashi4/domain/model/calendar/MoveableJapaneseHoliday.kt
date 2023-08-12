package jp.toastkid.yobidashi4.domain.model.calendar

import java.util.Calendar
import java.util.GregorianCalendar
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.HolidayCalendar

enum class MoveableJapaneseHoliday(val title: String, private val month: Int, val week: Int) {

    COMING_OF_AGE_DAY("成人の日", 1, 2),
    MARINE_DAY("海の日", 7, 3),
    RESPECT_FOR_THE_AGED_DAY("敬老の日", 9, 3),
    SPORTS_DAY("スポーツの日", 10, 2)
    ;


    companion object {
        private val months = values().map { it.month }.distinct()

        fun isTargetMonth(month: Int): Boolean {
            return months.contains(month)
        }

        fun find(year: Int, month: Int): List<Holiday> {
            if (months.contains(month).not()) {
                return emptyList()
            }

            when (month) {
                7 -> {
                    return mutableListOf(
                        Holiday(
                            MARINE_DAY.title,
                            month,
                            when (year) {
                                2020 -> 23
                                2021 -> 22
                                else -> calculateDate(year, month, MARINE_DAY.week)
                            },
                            HolidayCalendar.JAPAN.flag
                        )
                    ).also {
                        if (year == 2020 || year == 2021) {
                            Holiday(
                                SPORTS_DAY.title,
                                month,
                                when (year) {
                                    2020 -> 24
                                    2021 -> 23
                                    else -> -1
                                },
                                HolidayCalendar.JAPAN.flag
                            )
                        }
                    }
                }
                10 -> {
                    if (year == 2020 || year == 2021) {
                        return emptyList()
                    }
                }
            }

            val targetDay = values().firstOrNull { it.month == month } ?: return emptyList()

            return listOf(
                Holiday(
                    targetDay.title,
                    month,
                    calculateDate(year, month, targetDay.week),
                    "\uD83C\uDDEF\uD83C\uDDF5"
                )
            )
        }

        private fun calculateDate(year: Int, month: Int, week: Int): Int {
            val localDate = GregorianCalendar(year, month - 1, 1)
            val dayOfWeek = localDate.get(Calendar.DAY_OF_WEEK)
            val d = if (dayOfWeek == Calendar.MONDAY) {
                1
            } else {
                Calendar.SUNDAY - (dayOfWeek - 2)
            }

            val offset = if (dayOfWeek <= Calendar.MONDAY) {
                1
            } else {
                0
            }
            return d + (7 * (week - offset))
        }

    }

}