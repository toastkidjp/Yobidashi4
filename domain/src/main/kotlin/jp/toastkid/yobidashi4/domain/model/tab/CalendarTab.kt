package jp.toastkid.yobidashi4.domain.model.tab

import java.time.LocalDate

class CalendarTab(
    private val year: Int = LocalDate.now().year,
    private val month: Int = LocalDate.now().month.value
): Tab {

    override fun title(): String {
        return "Calendar"
    }

    override fun closeable(): Boolean {
        return true
    }

    override fun iconPath(): String {
        return "images/icon/ic_calendar.xml"
    }

    fun localDate() = LocalDate.of(year, month, 1)

}