package jp.toastkid.yobidashi4.domain.service.calendar


class SpecialCaseOffDayCalculatorService {

    /**
     *
     * @return isOffDay, forceNormal
     */
    operator fun invoke(year: Int, month: Int, date: Int): Pair<Boolean, Boolean> {
        if (TARGET_YEARS.contains(year).not() || TARGET_MONTHS.contains(month).not()) {
            return false to false
        }

        if (year == 2019) {
            if (month == 4 && date == 30) {
                return true to false
            }
            if (month == 5 && (date == 1 || date == 2)) {
                return true to false
            }
        }

        if (year == 2020) {
            if (month == 7) {
                return when (date) {
                    23, 24 -> return true to true
                    else -> false to true
                }
            }
            if (month == 8) {
                return (date == 10) to true
            }
            if (month == 10) {
                return false to true
            }
        }

        if (year == 2021) {
            if (month == 7) {
                return when (date) {
                    22, 23 -> return true to true
                    else -> false to true
                }
            }
            if (month == 8) {
                return (date == 9) to true
            }
            if (month == 10) {
                return false to true
            }
        }
        return false to false
    }

    companion object {
        private val TARGET_YEARS = setOf(2019, 2020, 2021)
        private val TARGET_MONTHS = setOf(4, 5, 7, 8, 10)
    }

}
