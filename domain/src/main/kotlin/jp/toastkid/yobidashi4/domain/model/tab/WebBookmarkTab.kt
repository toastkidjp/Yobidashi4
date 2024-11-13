package jp.toastkid.yobidashi4.domain.model.tab

class WebBookmarkTab(private val scrollPosition: Int = 0) : ScrollableContentTab {

    override fun title(): String = "Bookmark"

    override fun scrollPosition(): Int = scrollPosition

    override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
        return WebBookmarkTab(scrollPosition)
    }

}