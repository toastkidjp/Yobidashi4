package jp.toastkid.yobidashi4.domain.model.web.bookmark

import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class BookmarkTest {

    @Test
    fun fromWebTab() {
        val title = "title"
        val url = "https://test.co.jp"

        val bookmark = Bookmark.fromWebTab(WebTab(title, url))

        assertEquals(title, bookmark.title)
        assertEquals(url, bookmark.url)
        assertEquals("root", bookmark.parent)
        assertFalse(bookmark.folder)
    }

}