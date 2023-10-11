package jp.toastkid.yobidashi4.domain.model.web.search

import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

enum class SearchSite(val siteName: String, private val searchUrlBase: String, private val imageFileName: String = "") {
    YAHOO_JAPAN("Yahoo! JAPAN", "https://search.yahoo.co.jp/search?p=", "ic_yahoo_japan_logo.xml"),
    WIKIPEDIA("Wikipedia", "https://ja.wikipedia.org/w/index.php?search=", "ic_wikipedia.xml"),
    GOOGLE_MAP("Google Map", "https://www.google.co.jp/maps/search/", "ic_google_map.webp"),
    IMAGE_YAHOO_JAPAN("Image (Yahoo! JAPAN)", "https://search.yahoo.co.jp/image/search?p=", "ic_yahoo_japan_image_search.png"),
    YOUTUBE("YouTube",  "https://www.youtube.com/results?search_query=", "ic_video.xml"),
    REALTIME_YAHOO_JAPAN("Realtime (Yahoo! JAPAN)", "https://search.yahoo.co.jp/realtime/search?p=", "ic_yahoo_japan_realtime_search.webp"),
    FILMARKS("Filmarks", "https://filmarks.com/search/movies?q=", "ic_filmarks.png"),
    AMAZON("Amazon", "https://www.amazon.co.jp/s?k=", "ic_amazon.xml"),
    GITHUB("GitHub", "https://github.com/search?utf8=%E2%9C%93&type=&q=", "ic_github.xml"),
    SEARCH_WITH_IMAGE("Search with image", "https://www.bing.com/images/search?view=detailv2&iss=sbi&q=", "ic_image.xml"),
    ;

    fun make(rawQuery: String): URI {
        val additional = if (this == SEARCH_WITH_IMAGE
            && (rawQuery.startsWith("https://") || rawQuery.startsWith("http://"))) "imgurl:" else ""
        return URI("$searchUrlBase$additional${URLEncoder.encode(rawQuery, StandardCharsets.UTF_8.name())}")
    }

    fun iconPath() = "$IMAGE_FILE_FOLDER/$imageFileName"

    companion object {
        fun getDefault() = YAHOO_JAPAN
    }

}

private const val IMAGE_FILE_FOLDER = "images/search_site"