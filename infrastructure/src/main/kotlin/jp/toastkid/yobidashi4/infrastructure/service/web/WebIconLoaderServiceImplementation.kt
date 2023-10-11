package jp.toastkid.yobidashi4.infrastructure.service.web

import java.net.MalformedURLException
import java.net.URL
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.service.web.WebIconLoaderService
import jp.toastkid.yobidashi4.infrastructure.service.web.icon.IconUrlFinder
import jp.toastkid.yobidashi4.infrastructure.service.web.icon.WebIconDownloader
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory

@Single
class WebIconLoaderServiceImplementation : WebIconLoaderService {

    private val webIconDownloader = WebIconDownloader()

    override operator fun invoke(htmlSource: String, browserUrl: String?) {
        val iconUrls = IconUrlFinder().invoke(htmlSource).toMutableList()

        val webIcon = WebIcon()
        webIcon.makeFolderIfNeed()

        val targetUrl = try {
            URL(browserUrl)
        } catch (e: MalformedURLException) {
            LoggerFactory.getLogger(javaClass).warn("URL is malformed.", e)
            return
        }

        if (iconUrls.isEmpty()) {
            iconUrls.add("${targetUrl.protocol}://${targetUrl.host}/favicon.ico")
        }

        if (iconUrls.size > 1) {
            iconUrls.removeIf { it.endsWith(".ico") }
        }

        iconUrls.map {
            if (it.startsWith("/")) {
                "${targetUrl.protocol}://${targetUrl.host}$it"
            } else {
                it
            }
        }
            .forEach { webIconDownloader(URL(it), webIcon.faviconFolder(), targetUrl.host) }
    }

}