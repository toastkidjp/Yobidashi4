package jp.toastkid.yobidashi4.presentation.main.content.data

import androidx.compose.runtime.Immutable
import java.nio.file.Files
import java.nio.file.Path
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

@Immutable
data class FileListItem(
    val path: Path,
    val selected: Boolean = false,
    val editable: Boolean = false,
) {

    private val subText = AtomicReference("")

    private val sortKey = AtomicLong(-1L)

    init {
        subText.set(makeSubText())
    }

    private fun makeSubText(): String? {
        if (Files.exists(path).not()) {
            return null
        }

        val size = Files.size(path).toDouble()
        val unit = if (size > 1_000_000) "MB" else "KB"
        val displaySize = decimalFormat.format(size / (if (size > 1_000_000) 1_000_000 else 1000))
        val lastModifiedTime = Files.getLastModifiedTime(path)
        sortKey.set(lastModifiedTime.toMillis())
        return "$displaySize $unit | ${
            LocalDateTime
                .ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault())
                .format(dateTimeFormatter)
        }"
    }

    fun reverseSelection() = FileListItem(path, selected.not(), editable)

    fun unselect() = FileListItem(path, false, editable)

    fun subText(): String? = subText.get()

    fun sortKey(): Long = sortKey.get()

    fun keep() = !subText.get().isNullOrBlank()

}

private val decimalFormat = DecimalFormat("#,###.##")

private val dateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd(E) HH:mm:ss").withLocale(Locale.ENGLISH)
