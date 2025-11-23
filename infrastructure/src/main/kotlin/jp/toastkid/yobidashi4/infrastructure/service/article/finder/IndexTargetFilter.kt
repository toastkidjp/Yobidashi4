package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

class IndexTargetFilter(private val indexDirectoryPath: Path) {

    private val lastIndexed = calculateLastUpdated()

    private val targetExtensions = setOf("txt", "md")

    operator fun invoke(it: Path): Boolean {
        return Files.isDirectory(it).not()
                && Files.isReadable(it)
                && targetExtensions.contains(it.extension)
                && lastModifiedMs(it) > lastIndexed
    }

    private fun calculateLastUpdated() =
        Files.list(indexDirectoryPath)
            .filter { it.nameWithoutExtension.startsWith("segments_") }
            .map { lastModifiedMs(it) }
            .max(Comparator.naturalOrder())
            .orElseGet { 0L }

    private fun lastModifiedMs(paths: Path): Long = Files.getLastModifiedTime(paths).toMillis()

}