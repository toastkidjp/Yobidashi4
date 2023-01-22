package jp.toastkid.yobidashi4.domain.service.web

import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import org.jsoup.Jsoup

class WebIconLoaderService {

    operator fun invoke(htmlSource: String, browserUrl: String?) {
        val iconUrls = Jsoup.parse(htmlSource).select("link").filter { elem -> elem.attr("rel").contains("icon") }.map { it.attr("href") }
        val faviconFolder = Paths.get("data/web/icon")
        if (Files.exists(faviconFolder).not()) {
            Files.createDirectories(faviconFolder)
        }
        iconUrls.forEach {
            val fileExtension = URL(it).path.split(".").lastOrNull() ?: "png"
            val iconPath = faviconFolder.resolve("${URL(browserUrl).host}.$fileExtension")
            if (Files.exists(iconPath)) {
                return@forEach
            }
            val urlConnection = URI(it).toURL().openConnection()
            urlConnection.getInputStream().use {
                Files.write(iconPath, it.readAllBytes())
            }
        }
    }

}