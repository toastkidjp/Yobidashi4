package jp.toastkid.yobidashi4.infrastructure.repository.bookmark

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BookmarkFileStoreTest {

    @InjectMockKs
    private lateinit var bookmarkFileStore: BookmarkFileStore

    companion object {

        @MockK
        private lateinit var path: Path

        @JvmStatic
        @BeforeAll
        fun setUpAll() {
            MockKAnnotations.init(this)
            mockkStatic(Path::class)
            every { Path.of(any<String>()) }.returns(path)
            every { path.parent }.returns(path)
        }

        @JvmStatic
        @AfterAll
        fun tearDownAll() {
            unmockkAll()
        }

    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Files::class)
        every { Files.exists(any()) }.returns(true)
        every { Files.readAllLines(any()) }.returns(listOf("test\thttps://www.yahoo.co.jp"))
        every { Files.write(any(), any<Iterable<String>>()) }.returns(mockk())
        every { Files.createDirectories(any()) }.returns(mockk())
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun listEmptyCase() {
        every { Files.exists(any()) }.returns(false)

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

        verify(inverse = true) { Files.createDirectories(any()) }
        verify { Files.write(any(), any<Iterable<String>>()) }
    }

    @Test
    fun addNotFoundCase() {
        every { Files.exists(any()) }.returns(false)

        bookmarkFileStore.add(Bookmark("test", "https://www.yahoo.co.jp"))

        verify { Files.createDirectories(any()) }
        verify { Files.write(any(), any<Iterable<String>>()) }
    }
}