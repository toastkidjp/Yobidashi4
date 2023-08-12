package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday


class SpecialCaseOffDayCalculatorService {

    /**
     *
     * @return isOffDay, forceNormal
     */
    operator fun invoke(year: Int, month: Int): Set<Holiday> {
        if (TARGET_YEARS.contains(year).not() || TARGET_MONTHS.contains(month).not()) {
            return emptySet()
        }

        if (year == 2019) {
            if (month == 4) {
                return setOf(makeJapaneseHoliday("Special holiday", month, 30))
            }
            if (month == 5) {
                return setOf(
                    makeJapaneseHoliday("Special holiday", month, 1),
                    makeJapaneseHoliday("Special holiday", month, 2)
                )
            }
        }

        if (year == 2020) {
            if (month == 7) {
                return setOf(
                    makeJapaneseHoliday("Marine day", month, 23),
                    makeJapaneseHoliday("Sports day", month, 24)
                )
            }
        }

        if (year == 2021) {
            if (month == 7) {
                return setOf(
                    makeJapaneseHoliday("Marine day", month, 22),
                    makeJapaneseHoliday("Sports day", month, 23)
                )
            }
        }
        return emptySet()
    }

    private fun makeJapaneseHoliday(title: String, month: Int, day: Int) = Holiday(title, month,
        day, "\uD83C\uDDEF\uD83C\uDDF5")

    companion object {
        private val TARGET_YEARS = setOf(2019, 2020, 2021)
        private val TARGET_MONTHS = setOf(4, 5, 7, 8, 10)
    }

}
