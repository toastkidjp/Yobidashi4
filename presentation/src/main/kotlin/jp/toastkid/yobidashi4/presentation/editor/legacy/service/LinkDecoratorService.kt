package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import org.jsoup.Jsoup

class LinkDecoratorService {

    operator fun invoke(link: String): String {
        val url = try {
            URL(link)
        } catch (e: MalformedURLException) {
            return link
        }

        val title = try {
            Jsoup.parse(url, 3000).title()
        } catch (e: IOException) {
            return link
        }
        return "[$title]($link)"
    }

}