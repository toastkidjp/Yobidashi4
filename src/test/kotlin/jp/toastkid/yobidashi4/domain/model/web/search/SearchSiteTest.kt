package jp.toastkid.yobidashi4.domain.model.web.search

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SearchSiteTest {

    @Test
    fun make() {
        assertEquals(
            "https://www.bing.com/images/search?view=detailv2&iss=sbi&q=test",
            SearchSite.SEARCH_WITH_IMAGE.make("test").toString()
        )
        assertEquals(
            "https://www.bing.com/images/search?view=detailv2&iss=sbi&q=imgurl:https%3A%2F%2Fwww.yahoo.co.jp%2Ffavicon.ico",
            SearchSite.SEARCH_WITH_IMAGE.make("https://www.yahoo.co.jp/favicon.ico").toString()
        )
    }

    @Test
    fun iconPath() {
        SearchSite.values().forEach { assertTrue(it.iconPath().isNotBlank()) }
    }

}