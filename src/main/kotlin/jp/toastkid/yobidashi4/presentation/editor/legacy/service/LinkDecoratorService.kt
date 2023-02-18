package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern
import org.jsoup.Jsoup

class LinkDecoratorService {

    operator fun invoke(link: String): String {
        val url = try {
            URL(link)
        } catch (e: MalformedURLException) {
            return link
        }

        val title = Jsoup.parse(url, 3000).title()
        return "[$title]($link)"
    }

    companion object {

        private val PATTERN = Pattern.compile(
                "<title.*>(.+?)</title>",
                Pattern.DOTALL or Pattern.CASE_INSENSITIVE
        )

    }

}