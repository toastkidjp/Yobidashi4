package jp.toastkid.yobidashi4.infrastructure.repository

import java.nio.file.Files
import java.nio.file.Paths
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import org.koin.core.annotation.Single

@Single
class BookmarkFileStore : BookmarkRepository {

    override fun list(): List<Bookmark> {
        if (Files.exists(path).not()) {
            return emptyList()
        }

        return Files.readAllLines(path).map {
            val split = it.split("\t")
            Bookmark(
                title = split[0],
                url = split[1]
            )
        }
    }

    override fun add(item: Bookmark) {
        save(list().plus(item))
    }

    private fun save(list: List<Bookmark>) {
        if (Files.exists(path.parent).not()) {
            Files.createDirectories(path.parent)
        }

        Files.write(path, list.map { "${it.title}\t${it.url}" })
    }

}

private val path = Paths.get("user/bookmark/list.tsv")
