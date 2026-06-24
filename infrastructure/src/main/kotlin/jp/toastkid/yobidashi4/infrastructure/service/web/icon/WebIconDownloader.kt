package jp.toastkid.yobidashi4.infrastructure.service.web.icon

import jp.toastkid.yobidashi4.infrastructure.repository.factory.HttpUrlConnectionFactory
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.io.BufferedInputStream
import java.net.URL
import java.nio.file.Path

class WebIconDownloader(
    private val fileSystem: FileSystem
) {

    private val httpUrlConnectionFactory = HttpUrlConnectionFactory()

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

        val iconPath = faviconFolder.resolve("$targetHost.$fileExtension").toOkioPath()
        if (fileSystem.exists(iconPath)) {
            return
        }
        val urlConnection = httpUrlConnectionFactory.invoke(iconUrl) ?: return
        if (urlConnection.responseCode != 200) {
            return
        }
        BufferedInputStream(urlConnection.inputStream).use {
            fileSystem.write(iconPath) {
                write(it.readAllBytes())
            }
        }
    }

}