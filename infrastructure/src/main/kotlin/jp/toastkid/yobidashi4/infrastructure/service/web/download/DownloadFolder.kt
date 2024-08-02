package jp.toastkid.yobidashi4.infrastructure.service.web.download

import java.nio.file.Files
import java.nio.file.Path
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.path.absolutePathString
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

class DownloadFolder {

    fun makeIfNeed() {
        if (Files.exists(downloadFolder)) {
            return
        }

        Files.createDirectories(downloadFolder)
    }

    fun assignAbsolutePath(suggestedName: String?): String? {
        suggestedName ?: return null

        return resolveUnusedPath(suggestedName).absolutePathString()
    }

    fun assignQuickStorePath(url: String): Path {
        val extension = AtomicReference(
            if (!url.contains(".")) ".png" else url.substring(url.lastIndexOf("."))
        )
        val lastIndexOf = extension.get().lastIndexOf("?")
        if (lastIndexOf != -1) {
            extension.set(extension.get().substring(0, lastIndexOf))
        }
        return resolveUnusedPath("${dateTimeFormatter.format(LocalDateTime.now())}${extension.get()}")
    }

    private fun resolveUnusedPath(suggestedName: String): Path {
        val suffix = AtomicInteger(0)
        val initialPath = Path.of(suggestedName)
        val candidatePath = AtomicReference(downloadFolder.resolve(makeCandidateName(initialPath, 0)))

        while (Files.exists(candidatePath.get())) {
            val newCandidateName =
                makeCandidateName(initialPath, suffix.incrementAndGet())
            candidatePath.set(downloadFolder.resolve(newCandidateName))
        }

        return candidatePath.get()
    }

    private fun makeCandidateName(path: Path, suffix: Int) =
        "${path.nameWithoutExtension}_${formatter.format(suffix)}${if (path.extension.isNotEmpty()) "." else ""}${path.extension}"

}

private val downloadFolder = Path.of("user/download")

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

private val formatter = DecimalFormat("###").also {
    it.minimumIntegerDigits = 3;
    it.maximumIntegerDigits = 3;
}
