package jp.toastkid.yobidashi4.domain.service.web

import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.jsoup.Jsoup

class WebIconLoaderService {

    operator fun invoke(htmlSource: String, browserUrl: String?) {
        val iconUrls = Jsoup.parse(htmlSource).select("link")
            .filter { elem -> elem.attr("rel").contains("icon") }
            .map { it.attr("href") }
            .toMutableList()
        val faviconFolder = Paths.get("data/web/icon")
        if (Files.exists(faviconFolder).not()) {
            Files.createDirectories(faviconFolder)
        }

        val targetUrl = URL(browserUrl)

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
            .forEach { download(it, faviconFolder, targetUrl) }
    }

    private fun download(
        it: String,
        faviconFolder: Path,
        targetUrl: URL
    ) {
        val fileExtension = URL(it).path.split(".").lastOrNull() ?: "png"
        val iconPath = faviconFolder.resolve("${targetUrl.host}.$fileExtension")
        if (Files.exists(iconPath)) {
            return
        }
        val urlConnection = URI(it).toURL().openConnection() as? HttpURLConnection ?: return
        if (urlConnection.responseCode != 200) {
            return
        }
        urlConnection.getInputStream().use {
            Files.write(iconPath, it.readAllBytes())
        }
    }

}