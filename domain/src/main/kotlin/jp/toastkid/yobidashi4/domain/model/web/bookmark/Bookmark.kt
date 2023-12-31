package jp.toastkid.yobidashi4.domain.model.web.bookmark

import jp.toastkid.yobidashi4.domain.model.tab.WebTab

data class Bookmark(
    val title: String = "",
    val url: String = "",
    val favicon: String = "",
    val parent: String = "root",
    val folder: Boolean = false
) {

    companion object {
        fun fromWebTab(webTab: WebTab): Bookmark {
            return Bookmark(webTab.title(), webTab.url())
        }

    }

}