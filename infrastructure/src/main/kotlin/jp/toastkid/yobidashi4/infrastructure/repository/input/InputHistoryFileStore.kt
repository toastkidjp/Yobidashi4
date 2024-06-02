package jp.toastkid.yobidashi4.infrastructure.repository.input

import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.repository.input.InputHistoryRepository
import org.koin.core.annotation.Single

@Single
class InputHistoryFileStore(private val context: String) : InputHistoryRepository {

    override fun list(): List<InputHistory> {
        return filter("")
    }

    override fun filter(query: String?): List<InputHistory> {
        val path = path()
        if (Files.exists(path).not()) {
            return emptyList()
        }

        return Files.readAllLines(path).filter { query.isNullOrBlank() || it.contains(query) }.mapNotNull {
            InputHistory.from(it)
        }
    }

    override fun add(item: InputHistory) {
        save(list().plus(item))
    }

    override fun delete(item: InputHistory) {
        save(list().minus(item))
    }

    override fun deleteWithWord(word: String) {
        save(list().filter { it.word != word })
    }

    override fun clear() {
        Files.deleteIfExists(path())
    }

    private fun save(list: List<InputHistory>) {
        if (Files.exists(path().parent).not()) {
            Files.createDirectories(path().parent)
        }

        Files.write(path(), list.sortedByDescending { it.timestamp }.distinctBy { it.word }.map { it.toTsv() })
    }

    private fun path(): Path {
        return folder.resolve("$context.$extension")
    }

}

private val folder = Path.of("temporary/input/history/")

private const val extension = "tsv"