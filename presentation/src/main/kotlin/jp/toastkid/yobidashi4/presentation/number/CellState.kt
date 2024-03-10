package jp.toastkid.yobidashi4.presentation.number

data class CellState(
    val number: Int = -1,
    val open: Boolean = false
) {

    fun text() = if (number == -1) "_" else "$number"

}