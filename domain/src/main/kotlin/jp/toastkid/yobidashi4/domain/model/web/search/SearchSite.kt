package jp.toastkid.yobidashi4.domain.model.web.search

import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import jp.toastkid.yobidashi4.domain.service.web.SiteSearchUrlGenerator

enum class SearchSite(val siteName: String, private val searchUrlBase: String) {
    YAHOO_JAPAN("Yahoo! JAPAN", "https://search.yahoo.co.jp/search?p="),
    WIKIPEDIA("Wikipedia", "https://ja.wikipedia.org/w/index.php?search="),
    GOOGLE_MAP("Google Map", "https://www.google.co.jp/maps/search/"),
    IMAGE_YAHOO_JAPAN("Image (Yahoo! JAPAN)", "https://search.yahoo.co.jp/image/search?p="),
    YOUTUBE("YouTube", "https://www.youtube.com/results?search_query="),
    REALTIME_YAHOO_JAPAN("Realtime (Yahoo! JAPAN)", "https://search.yahoo.co.jp/realtime/search?p="),
    FILMARKS("Filmarks", "https://filmarks.com/search/movies?q="),
    AMAZON("Amazon", "https://www.amazon.co.jp/s?k="),
    GITHUB("GitHub", "https://github.com/search?utf8=%E2%9C%93&type=&q="),
    SEARCH_WITH_IMAGE("Search with image", "https://www.bing.com/images/search?view=detailv2&iss=sbi&q="),
    SITE_SEARCH("Site Search", "https://www.google.com/search?q="),
    ;

    fun make(rawQuery: String, currentSiteUrl: String? = null): URI {
        if (this == SITE_SEARCH && currentSiteUrl != null) {
            return URI(SiteSearchUrlGenerator().invoke(rawQuery, currentSiteUrl))
        }

        val additional = if (this == SEARCH_WITH_IMAGE
            && (rawQuery.startsWith("https://") || rawQuery.startsWith("http://"))) "imgurl:" else ""
        return URI("$searchUrlBase$additional${URLEncoder.encode(rawQuery, StandardCharsets.UTF_8.name())}")
    }

    companion object {
        fun getDefault() = YAHOO_JAPAN
    }

}
