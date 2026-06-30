package jp.toastkid.yobidashi4.infrastructure.repository.bookmark

import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.bookmark.WebBookmarkPath
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.buffer
import org.koin.core.annotation.Single

@Single
class BookmarkFileStore(private val fileSystem: FileSystem) : BookmarkRepository {

    override fun list(): List<Bookmark> {
        if (fileSystem.exists(path.toOkioPath()).not()) {
            return emptyList()
        }

        return fileSystem.source(path.toOkioPath()).buffer().use {
            val lines = it.readUtf8().split("\n")
            lines
                .filter { line -> line.contains(SPLITTER) }
                .map(::toBookmark)
        }
    }

    private fun toBookmark(line: String): Bookmark {
        val split = line.split(SPLITTER)
        return Bookmark(
            title = split[0],
            url = split[1]
        )
    }

    override fun add(item: Bookmark) {
        save(list().plus(item))
    }

    private fun save(list: List<Bookmark>) {
        if (fileSystem.exists(path.parent.toOkioPath()).not()) {
            fileSystem.createDirectories(path.parent.toOkioPath())
        }

        fileSystem.sink(path.toOkioPath())
            .buffer()
            .use {
                it
                    .writeUtf8(list.joinToString("\n", transform = Bookmark::toTsv))
                    .emit()
            }

    }

    override fun delete(item: Bookmark) {
        save(list().minus(item))
    }

}

private val path = WebBookmarkPath().getPath()

private const val SPLITTER = "\t"
