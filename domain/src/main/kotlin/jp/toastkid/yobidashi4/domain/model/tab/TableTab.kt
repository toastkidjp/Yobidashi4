package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.aggregation.MovieMemoExtractorResult
import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult

data class TableTab(
    private val title: String,
    private val items: AggregationResult,
    private val closeable: Boolean = true,
    private val reloadAction: () -> Unit = {}
): Tab, Reloadable {

    override fun title(): String = title

    override fun closeable(): Boolean = closeable

    override fun iconPath(): String? {
        return when (items) {
            is MovieMemoExtractorResult -> "${ICON_FOLDER}ic_movie.xml"
            is OutgoAggregationResult -> "${ICON_FOLDER}ic_payments.xml"
            is FindResult -> "${ICON_FOLDER}ic_find_in_page.xml"
            else -> null
        }
    }

    override fun reload() {
        reloadAction()
    }

    fun items() = items

}

private const val ICON_FOLDER = "images/icon/"