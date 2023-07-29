package jp.toastkid.yobidashi4.domain.model.calendar

enum class MoveableJapaneseHoliday(private val month: Int, val week: Int) {

    COMING_OF_AGE_DAY(1, 2),
    MARINE_DAY(7, 3),
    RESPECT_FOR_THE_AGED_DAY(9, 3),
    SPORTS_DAY(10, 2)
    ;

    companion object {
        private val months = values().map { it.month }.distinct()

        fun isTargetMonth(month: Int): Boolean {
            return months.contains(month)
        }

        fun find(month: Int): MoveableJapaneseHoliday? {
            return values().firstOrNull { it.month == month }
        }

    }

}