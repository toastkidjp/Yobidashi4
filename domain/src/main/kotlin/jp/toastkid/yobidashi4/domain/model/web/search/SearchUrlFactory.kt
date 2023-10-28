package jp.toastkid.yobidashi4.domain.model.web.search

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class SearchUrlFactory {

    operator fun invoke(query: String): String =
        if (isUrl(query))
            query
        else
            "https://search.yahoo.co.jp/search?p=${encodeUtf8(query)}"

    private fun isUrl(text: String) = text.startsWith("http://") || text.startsWith("https://")

    private fun encodeUtf8(selectedText: String) = URLEncoder.encode(selectedText, StandardCharsets.UTF_8.name())

}