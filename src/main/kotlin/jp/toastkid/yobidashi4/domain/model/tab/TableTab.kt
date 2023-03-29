package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.MovieMemoExtractorResult

data class TableTab(
    private val title: String,
    val items: AggregationResult,
    private val closeable: Boolean = true
): Tab {

    override fun title(): String = title

    override fun closeable(): Boolean = closeable

    override fun iconPath(): String? {
        return when (items) {
            is MovieMemoExtractorResult -> "${ICON_FOLDER}ic_movie.xml"
            else -> null
        }
    }

}

private val ICON_FOLDER = "images/icon/"