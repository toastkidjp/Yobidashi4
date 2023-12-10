package jp.toastkid.yobidashi4.presentation.main.title

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class LauncherJarTimestampReader {

    operator fun invoke(resource: String = System.getProperty("java.class.path")): String? {
        if (resource.contains(";")) {
            return null
        }

        val path = Path.of(resource)
        if (Files.exists(path).not()) {
            return null
        }

        return DateTimeFormatter.ofPattern("yyyy-MM-dd(E) HH:mm:ss").withLocale(Locale.ENGLISH)
            .format(LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault()))
    }

}