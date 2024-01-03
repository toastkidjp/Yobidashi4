package jp.toastkid.yobidashi4.presentation.main.content.data

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class FileListItem(
    val path: Path,
    val selected: Boolean = false,
    val editable: Boolean = false
) {

    fun reverseSelection() = FileListItem(path, selected.not(), editable)

    fun unselect() = FileListItem(path, false, editable)

    fun subText(): String? {
        if (Files.exists(path).not()) {
            return null
        }

        return "${Files.size(path) / 1000} KB | ${
            LocalDateTime
                .ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault())
                .format(dateTimeFormatter)
        }"
    }

}

private val dateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd(E) HH:mm:ss").withLocale(Locale.ENGLISH)
