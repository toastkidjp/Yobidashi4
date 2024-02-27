package jp.toastkid.yobidashi4.domain.model.tab

class WebBookmarkTab(private val scrollPosition: Int = 0) : ScrollableContentTab {

    override fun title(): String = "Bookmark"

    override fun closeable(): Boolean = true

    override fun iconPath(): String {
        return "images/icon/ic_bookmark.xml"
    }

    override fun scrollPosition(): Int = scrollPosition

    override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
        return WebBookmarkTab(scrollPosition)
    }

}