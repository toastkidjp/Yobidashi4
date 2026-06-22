package jp.toastkid.yobidashi4.infrastructure.repository.input

import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.repository.input.InputHistoryRepository
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.buffer
import org.koin.core.annotation.Single
import java.nio.file.Path

@Single
class InputHistoryFileStore(private val fileSystem: FileSystem, private val context: String) : InputHistoryRepository {

    override fun list(): List<InputHistory> {
        return filter("")
    }

    override fun filter(query: String?): List<InputHistory> {
        val path = path()
        if (fileSystem.exists(path.toOkioPath()).not()) {
            return emptyList()
        }

        return fileSystem.source(path.toOkioPath()).buffer().use { buffer ->
            buffer.readUtf8().split("\n").filter { query.isNullOrBlank() || it.contains(query) }
                .mapNotNull(InputHistory::from)
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
        fileSystem.delete(path().toOkioPath())
    }

    private fun save(list: List<InputHistory>) {
        if (fileSystem.exists(path().parent.toOkioPath()).not()) {
            fileSystem.createDirectories(path().parent.toOkioPath())
        }

        fileSystem.sink(path().toOkioPath()).buffer().use {
            it.writeUtf8(
                list.sortedByDescending(InputHistory::timestamp).distinctBy { it.word }.map(InputHistory::toTsv)
                    .joinToString("\n")
            )
        }
    }

    private fun path(): Path {
        return folder.resolve("$context.$extension")
    }

}

private val folder = Path.of("temporary/input/history/")

private const val extension = "tsv"