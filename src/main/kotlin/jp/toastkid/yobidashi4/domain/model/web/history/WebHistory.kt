package jp.toastkid.yobidashi4.domain.model.web.history

data class WebHistory(
    val title: String,
    val url: String,
    val lastVisitedTime: Long = -1,
    val visitingCount: Int = 1
) {
    fun toTsv() = "$title$DELIMITER$url$DELIMITER$lastVisitedTime$DELIMITER$visitingCount"

}

const val DELIMITER = "\t"