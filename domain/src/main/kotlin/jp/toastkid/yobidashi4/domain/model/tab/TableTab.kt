package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult

data class TableTab(
    private val title: String,
    private val items: AggregationResult,
    private val closeable: Boolean = true,
    private val scrollPosition: Int = 0,
    private val reloadAction: () -> Unit = {}
): ScrollableContentTab, Reloadable {

    override fun title(): String = title

    override fun closeable(): Boolean = closeable

    override fun reload() {
        reloadAction()
    }

    fun items() = items

    override fun scrollPosition(): Int = scrollPosition

    override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
        return this.copy(scrollPosition = scrollPosition)
    }

}
