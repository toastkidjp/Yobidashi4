package jp.toastkid.yobidashi4.infrastructure.service.editor

import jp.toastkid.yobidashi4.domain.service.editor.LinkDecoratorService
import org.jsoup.Jsoup
import org.koin.core.annotation.Single
import java.io.IOException
import java.net.URI

@Single
class LinkDecoratorServiceImplementation : LinkDecoratorService {

    override operator fun invoke(link: String): String {
        val trimmedLink = link.trim()
        val url = try {
            URI(trimmedLink).toURL()
        } catch (e: IllegalArgumentException) {
            return link
        }

        val title = try {
            Jsoup.parse(url, 3000).title()
        } catch (e: IOException) {
            return link
        }
        return "[$title]($trimmedLink)"
    }

}
