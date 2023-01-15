package jp.toastkid.yobidashi4.domain.model.calendar

import java.time.DayOfWeek

data class CalendarDate(
    val date: Int,
    val dayOfWeek: DayOfWeek,
    val label: String = "",
    val offDay: Boolean = false
) {
}