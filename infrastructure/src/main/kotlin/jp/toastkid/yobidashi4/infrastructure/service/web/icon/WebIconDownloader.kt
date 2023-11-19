package jp.toastkid.yobidashi4.infrastructure.service.web.icon

import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

class WebIconDownloader {

    operator fun invoke(
        iconUrl: URL,
        faviconFolder: Path,
        targetHost: String?
    ) {
        val fileExtension = (
                if (iconUrl.path.contains(".") && iconUrl.path.endsWith(".").not())
                    iconUrl.path.split(".").lastOrNull()
                else null
                ) ?: "png"

        val iconPath = faviconFolder.resolve("$targetHost.$fileExtension")
        if (Files.exists(iconPath)) {
            return
        }
        val urlConnection = urlConnection(iconUrl) ?: return
        if (urlConnection.responseCode != 200) {
            return
        }
        BufferedInputStream(urlConnection.inputStream).use {
            Files.write(iconPath, it.readAllBytes())
        }
    }

    fun urlConnection(iconUrl: URL) = iconUrl.openConnection() as? HttpURLConnection

}