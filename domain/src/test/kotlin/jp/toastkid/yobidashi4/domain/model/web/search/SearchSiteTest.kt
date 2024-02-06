package jp.toastkid.yobidashi4.domain.model.web.search

import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.service.web.SiteSearchUrlGenerator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SearchSiteTest {

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

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
    fun siteSearchCase() {
        mockkConstructor(SiteSearchUrlGenerator::class)
        every { anyConstructed<SiteSearchUrlGenerator>().invoke(any(), any()) } returns "https://test.site.search.com"

        val uri = SearchSite.SITE_SEARCH.make("test", "https://www.yahoo.co.jp")

        assertEquals("https://test.site.search.com", uri.toString())
    }

    @Test
    fun workaroundSiteSearchCase() {
        mockkConstructor(SiteSearchUrlGenerator::class)
        every { anyConstructed<SiteSearchUrlGenerator>().invoke(any(), any()) } returns "https://test.site.search.com"

        val uri = SearchSite.SITE_SEARCH.make("test", null)

        assertEquals("https://www.google.com/search?q=test", uri.toString())
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