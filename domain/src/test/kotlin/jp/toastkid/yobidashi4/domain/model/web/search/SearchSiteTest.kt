package jp.toastkid.yobidashi4.domain.model.web.search

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
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
            "https://www.bing.com/images/search?view=detailv2&iss=sbi&q=imgurl:http%3A%2F%2Fwww.yahoo.co.jp%2Ffavicon.ico",
            SearchSite.SEARCH_WITH_IMAGE.make("http://www.yahoo.co.jp/favicon.ico").toString()
        )
        assertEquals(
            "https://www.bing.com/images/search?view=detailv2&iss=sbi&q=imgurl:https%3A%2F%2Fwww.yahoo.co.jp%2Ffavicon.ico",
            SearchSite.SEARCH_WITH_IMAGE.make("https://www.yahoo.co.jp/favicon.ico").toString()
        )
        assertEquals("https://filmarks.com/search/movies?q=Up", SearchSite.FILMARKS.make("Up").toString())
    }

    @Test
    fun iconPath() {
        SearchSite.values().forEach { assertTrue(it.iconPath().isNotBlank()) }
    }

    @Test
    fun getDefault() {
        assertSame(SearchSite.YAHOO_JAPAN, SearchSite.getDefault())
    }

}