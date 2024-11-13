package jp.toastkid.yobidashi4.domain.model.tab

import java.time.LocalDate

class CalendarTab(
    private val year: Int = LocalDate.now().year,
    private val month: Int = LocalDate.now().month.value
): Tab {

    override fun title(): String {
        return "Calendar"
    }

    fun localDate(): LocalDate = LocalDate.of(year, month, 1)

}