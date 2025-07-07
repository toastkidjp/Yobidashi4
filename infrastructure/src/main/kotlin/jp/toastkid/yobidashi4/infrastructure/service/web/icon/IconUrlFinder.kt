package jp.toastkid.yobidashi4.infrastructure.service.web.icon

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class IconUrlFinder {

    operator fun invoke(htmlSource: String) =
        Jsoup.parse(htmlSource)
            .select("link")
            .filter { elem -> extractIcon(elem) }
            .map { it.attr("href") }

    private fun extractIcon(elem: Element) = elem.attr("rel").contains("icon")

}