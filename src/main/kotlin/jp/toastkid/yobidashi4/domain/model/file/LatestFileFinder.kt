package jp.toastkid.yobidashi4.domain.model.file

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.stream.Collectors

class LatestFileFinder {

    operator fun invoke(path: Path, latest: LocalDateTime): MutableList<Path> {
        val toEpochMilli =  latest.toInstant(OffsetDateTime.now().offset).toEpochMilli()
        return Files.list(path)
            .sorted { p1, p2 -> Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2)) * -1 }
            .filter {
                Files.getLastModifiedTime(it).toMillis() > toEpochMilli
            }
            .collect(Collectors.toList())
    }

}