package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.aggregation.MovieMemoExtractorResult
import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.StocksAggregationResult

data class TableTab(
    private val title: String,
    private val items: AggregationResult,
    private val closeable: Boolean = true,
    private val scrollPosition: Int = 0,
    private val reloadAction: () -> Unit = {}
): ScrollableContentTab, Reloadable {

    override fun title(): String = title

    override fun closeable(): Boolean = closeable

    override fun iconPath(): String? {
        return when (items) {
            is MovieMemoExtractorResult -> "${ICON_FOLDER}ic_movie.xml"
            is OutgoAggregationResult -> "${ICON_FOLDER}ic_payments.xml"
            is FindResult -> "${ICON_FOLDER}ic_find_in_page.xml"
            is StocksAggregationResult -> "${ICON_FOLDER}ic_asset_management.xml"
            else -> null
        }
    }

    override fun reload() {
        reloadAction()
    }

    fun items() = items

    override fun scrollPosition(): Int = scrollPosition

    override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
        return this.copy(scrollPosition = scrollPosition)
    }

}

private const val ICON_FOLDER = "images/icon/"