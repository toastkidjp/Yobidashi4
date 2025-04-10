package jp.toastkid.yobidashi4.domain.service.media

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.isDirectory
import kotlin.io.path.isExecutable
import kotlin.io.path.nameWithoutExtension

class MediaFileFinder {

    operator fun invoke(mediaFileFolderPath: String): Collection<Path> {
        val folder = Path.of(mediaFileFolderPath)
        if (folder.isDirectory().not()) {
            return emptyList()
        }

        return readFromFolder(folder).union(
            Files.list(folder)
                .filter(Path::isDirectory)
                .map(::readFromFolder)
                .flatMap { it.stream() }
                .collect(Collectors.toList())
        )
    }

    private fun readFromFolder(folder: Path): List<Path> {
        return Files.list(folder)
            .filter(::isKeep)
            .collect(Collectors.toList())
    }

    private fun isKeep(it: Path) = (it.isDirectory().not()
            && prefixes.all { prefix -> it.nameWithoutExtension.startsWith(prefix).not() }
            && it.isExecutable())

}

private val prefixes = setOf("AlbumArt", "Folder", "iTunes", "desktop")
