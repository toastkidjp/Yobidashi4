package jp.toastkid.yobidashi4.infrastructure.service.editor

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import jp.toastkid.yobidashi4.domain.service.editor.LinkDecoratorService
import org.jsoup.Jsoup
import org.koin.core.annotation.Single

@Single
class LinkDecoratorServiceImplementation : LinkDecoratorService {

    override operator fun invoke(link: String): String {
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
