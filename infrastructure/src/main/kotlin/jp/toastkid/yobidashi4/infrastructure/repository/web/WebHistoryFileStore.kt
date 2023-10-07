package jp.toastkid.yobidashi4.infrastructure.repository.web

import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import org.koin.core.annotation.Single

@Single
class WebHistoryFileStore : WebHistoryRepository {

    override fun add(title: String, url: String) {
        val webHistories = readAll()
        val item = webHistories
            .firstOrNull { it.title == title && it.url == url }
            ?.let { it.copy(lastVisitedTime = System.currentTimeMillis(), visitingCount = it.visitingCount + 1) }
            ?: WebHistory(title, url, System.currentTimeMillis())

        val key = title + url
        val filtered = webHistories.filter { it.title + it.url != key }
        val newList = mutableListOf<WebHistory>().also {
            it.addAll(filtered)
            it.add(item)
        }

        Files.write(getPath(), newList.map { it.toTsv() })
    }

    override fun delete(title: String, url: String) {
        Files.write(
            getPath(),
            readAll().dropWhile { it.title == title && it.url == url }.map { it.toTsv() }
        )
    }

    override fun readAll(): List<WebHistory> {
        val path = getPath()
        makeFolderIfNeed(path)

        if (Files.exists(path).not()) {
            return emptyList()
        }

        return Files.readAllLines(path).filter { it.isNotBlank() }.map {
            val split = it.split("\t")
            WebHistory(
                split[0],
                if (split.size >= 2) split[1] else "",
                if (split.size >= 3) split[2].toLongOrNull() ?: 0 else 0,
                if (split.size >= 4) split[3].toIntOrNull() ?: 0 else 0
            )
        }
    }

    private fun getPath(): Path {
        return Path.of(PATH_TO_HISTORY)
    }

    private fun makeFolderIfNeed(path: Path) {
        if (Files.exists(path.parent).not()) {
            Files.createDirectories(path.parent)
        }
    }

    override fun clear() {
        Files.delete(getPath())
    }

}

private const val PATH_TO_HISTORY = "temporary/web/history.tsv"