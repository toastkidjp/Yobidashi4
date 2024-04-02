package jp.toastkid.yobidashi4.domain.model.web.icon

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.pathString

class WebIcon {

    private val faviconFolder = Path.of("temporary/web/icon")

    fun makeFolderIfNeed() {
        if (Files.exists(faviconFolder).not()) {
            Files.createDirectories(faviconFolder)
        }
    }

    fun faviconFolder(): Path = faviconFolder

    fun find(url: String): Path? {
        if (!url.startsWith("http")) {
            return null
        }

        return Files.list(faviconFolder).collect(Collectors.toList()).firstOrNull {
            val startsWith = it.fileName.pathString.startsWith(URL(url).host.trim())
            startsWith
        }
    }

    fun readAll(): List<Path> =
        Files.list(faviconFolder).collect(Collectors.toList())

}