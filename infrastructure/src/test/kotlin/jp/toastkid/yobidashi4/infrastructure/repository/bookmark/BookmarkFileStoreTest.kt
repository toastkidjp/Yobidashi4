package jp.toastkid.yobidashi4.infrastructure.repository.bookmark

import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.bookmark.WebBookmarkPath
import okio.BufferedSource
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BookmarkFileStoreTest {

    private lateinit var bookmarkFileStore: BookmarkFileStore

    private lateinit var fakeFileSystem: FakeFileSystem

    private lateinit var filePath: Path
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        fakeFileSystem = FakeFileSystem()

        val path = WebBookmarkPath().getPath()
        val folderName = path.parent.toString()
        fakeFileSystem.createDirectories(folderName.toPath())
        filePath = "$folderName/${path.fileName}".toPath()
        fakeFileSystem.write(filePath) { writeUtf8("test\thttps://www.yahoo.co.jp") }

        bookmarkFileStore = BookmarkFileStore(fakeFileSystem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun listEmptyCase() {
        val path = WebBookmarkPath().getPath()
        val folderName = path.parent.toString()
        fakeFileSystem.delete(filePath)
        fakeFileSystem.delete(folderName.toPath())

        assertTrue(bookmarkFileStore.list().isEmpty())
    }

    @Test
    fun list() {
        val bookmarks = bookmarkFileStore.list()
        assertTrue(bookmarks.isNotEmpty())
        assertEquals("test", bookmarks.first().title)
        assertEquals("https://www.yahoo.co.jp", bookmarks.first().url)
    }

    @Test
    fun add() {
        bookmarkFileStore.add(Bookmark("test", "https://www.yahoo.co.jp"))

        assertEquals(
            2,
            fakeFileSystem.source(filePath)
                .buffer()
                .use {
                    it.readUtf8()
                        .split("\n")
                        .size
                }
        )
    }

    @Test
    fun addNotFoundCase() {
        val path = WebBookmarkPath().getPath()
        val folderName = path.parent.toString()
        fakeFileSystem.delete(filePath)
        fakeFileSystem.delete(folderName.toPath())

        bookmarkFileStore.add(Bookmark("test", "https://www.yahoo.co.jp"))

        assertEquals(
            "test\thttps://www.yahoo.co.jp",
            fakeFileSystem.source(filePath)
                .buffer()
                .use {
                    it.readUtf8()
                }
        )
    }

    @Test
    fun delete() {
        val item = Bookmark("test", "https://www.yahoo.co.jp")

        bookmarkFileStore.delete(item)

        assertTrue(
            fakeFileSystem.source(filePath)
                .buffer()
                .use(BufferedSource::readUtf8)
                .trim()
                .isEmpty()
        )
    }

}