package jp.toastkid.yobidashi4.infrastructure.service.web.icon

import org.jsoup.Jsoup

class IconUrlFinder {

    operator fun invoke(htmlSource: String) =
        Jsoup.parse(htmlSource).select("link")
            .filter { elem -> elem.attr("rel").contains("icon") }
            .map { it.attr("href") }

}