package jp.toastkid.yobidashi4.infrastructure.service.web

import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.web.WebIconLoaderService
import jp.toastkid.yobidashi4.infrastructure.service.web.icon.IconUrlFinder
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory

@Single
class WebIconLoaderServiceImplementation : WebIconLoaderService {

    override operator fun invoke(htmlSource: String, browserUrl: String?) {
        val iconUrls = IconUrlFinder().invoke(htmlSource).toMutableList()
        val faviconFolder = Path.of("temporary/web/icon")
        if (Files.exists(faviconFolder).not()) {
            Files.createDirectories(faviconFolder)
        }

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
            .forEach { download(it, faviconFolder, targetUrl.host) }
    }

    override fun download(
        iconUrl: String,
        faviconFolder: Path,
        targetHost: String?
    ) {
        val fileExtension = try {
            URL(iconUrl).path.split(".").lastOrNull() ?: "png"
        } catch (e: MalformedURLException) {
            LoggerFactory.getLogger(javaClass).debug("Malformed URL: $iconUrl")
            return
        }

        val iconPath = faviconFolder.resolve("$targetHost.$fileExtension")
        if (Files.exists(iconPath)) {
            return
        }
        val urlConnection = URI(iconUrl).toURL().openConnection() as? HttpURLConnection ?: return
        if (urlConnection.responseCode != 200) {
            return
        }
        BufferedInputStream(urlConnection.inputStream).use {
            Files.write(iconPath, it.readAllBytes())
        }
    }

}