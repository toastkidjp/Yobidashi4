package jp.toastkid.yobidashi4.domain.model.tab

class WebHistoryTab(private val scrollPosition: Int = 0) : ScrollableContentTab {

    override fun title(): String {
        return "Web history"
    }

    override fun scrollPosition(): Int = scrollPosition

    override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
        return WebHistoryTab(scrollPosition)
    }

}