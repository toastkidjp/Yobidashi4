package jp.toastkid.yobidashi4.domain.model.tab

class WebBookmarkTab : Tab {

    override fun title(): String = "Bookmark"

    override fun closeable(): Boolean = true

    override fun iconPath(): String {
        return "images/icon/ic_bookmark.xml"
    }
}