package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult

data class TableTab(
    private val title: String,
    val items: AggregationResult,
    private val closeable: Boolean = true
): Tab {

    override fun title(): String = title

    override fun closeable(): Boolean = closeable

}