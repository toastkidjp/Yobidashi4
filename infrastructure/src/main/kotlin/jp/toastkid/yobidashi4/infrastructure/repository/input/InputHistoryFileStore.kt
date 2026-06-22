package jp.toastkid.yobidashi4.infrastructure.repository.input

import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.repository.input.InputHistoryRepository
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import org.koin.core.annotation.Single

@Single
class InputHistoryFileStore(
    private val fileSystem: FileSystem,
    private val context: String
) : InputHistoryRepository {

    override fun list(): List<InputHistory> {
        return filter("")
    }

    override fun filter(query: String?): List<InputHistory> {
        val path = path()
        if (fileSystem.exists(path).not()) {
            return emptyList()
        }

        return fileSystem.source(path).buffer().use { buffer ->
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
        fileSystem.delete(path())
    }

    private fun save(list: List<InputHistory>) {
        val parent = path().parent ?: return
        if (fileSystem.exists(parent).not()) {
            fileSystem.createDirectories(parent)
        }

        fileSystem.sink(path()).buffer().use {
            it.writeUtf8(
                list.sortedByDescending(InputHistory::timestamp).distinctBy { it.word }.map(InputHistory::toTsv)
                    .joinToString("\n")
            )
        }
    }

    private fun path(): Path {
        return folder.resolve("$context.$extension", false)
    }

}

private val folder = "temporary/input/history/".toPath()

private const val extension = "tsv"