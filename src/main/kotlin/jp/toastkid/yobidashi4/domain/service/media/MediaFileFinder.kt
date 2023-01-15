package jp.toastkid.yobidashi4.domain.service.media

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.io.path.isExecutable
import kotlin.io.path.nameWithoutExtension

class MediaFileFinder {

    operator fun invoke(mediaFileFolderPath: String): Collection<Path> {
        val folder = Paths.get(mediaFileFolderPath)
        if (folder.isDirectory().not()) {
            return emptyList()
        }

        return readFromFolder(folder).union(
            Files.list(folder)
                .filter { it.isDirectory() }
                .map { readFromFolder(it) }
                .flatMap { it.stream() }
                .toList()
        )
    }

    private fun readFromFolder(folder: Path): List<Path> {
        if (folder.isDirectory().not()) {
            return emptyList()
        }

        return Files.list(folder)
            .filter { it.isDirectory().not() && it.nameWithoutExtension.startsWith("AlbumArt").not() && it.isExecutable() }
            .toList()
    }

}