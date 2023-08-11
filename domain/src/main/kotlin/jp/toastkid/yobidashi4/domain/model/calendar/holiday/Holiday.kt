package jp.toastkid.yobidashi4.domain.model.calendar.holiday

data class Holiday(
    val title: String,
    val month: Int,
    val day: Int,
    val flag: String = "\uD83C\uDDFA\uD83C\uDDF8"
)