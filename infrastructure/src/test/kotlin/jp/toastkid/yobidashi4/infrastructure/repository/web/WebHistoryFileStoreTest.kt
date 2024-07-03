package jp.toastkid.yobidashi4.infrastructure.repository.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebHistoryFileStoreTest {

    @InjectMockKs
    private lateinit var webHistoryFileStore: WebHistoryFileStore

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var parent: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { path.parent } returns parent

        mockkStatic(Path::class)
        every { Path.of(any<String>()) } returns path

        mockkStatic(Files::class)
        every { Files.exists(path) } returns true
        every { Files.exists(parent) } returns true
        every { Files.createDirectories(any()) } returns parent
        every { Files.newInputStream(any()) } returns "Yahoo! JAPAN Test\thttps://www.yahoo.co.jp\t\t".byteInputStream()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun add() {
        val slot = slot<Iterable<CharSequence>>()
        every { Files.write(any(), capture(slot)) } returns path

        webHistoryFileStore.add("Yahoo! JAPAN Test2", "https://www.yahoo.co.jp")

        assertEquals(2, slot.captured.toMutableList().size)
        verify { Files.write(any(), slot.captured)  }
    }

    @Test
    fun addUpdatingCase() {
        val slot = slot<Iterable<CharSequence>>()
        every { Files.write(any(), capture(slot)) } returns path

        webHistoryFileStore.add("Yahoo! JAPAN Test", "https://www.yahoo.co.jp")

        assertEquals(1, slot.captured.toMutableList().size)
        verify { Files.write(any(), slot.captured)  }
    }

    @Test
    fun storeAll() {
        val slot = slot<Iterable<CharSequence>>()
        every { Files.write(any(), capture(slot)) } returns path

        webHistoryFileStore.storeAll(
            listOf(
                WebHistory("Storing", "https://storing.co.jp"),
                WebHistory("Storing2", "https://storing.co.jp"),
            )
        )

        assertEquals(2, slot.captured.toMutableList().size)
        verify { Files.write(any(), slot.captured)  }
    }

    @Test
    fun delete() {
        val slot = slot<Iterable<CharSequence>>()
        every { Files.write(any(), capture(slot)) } returns path

        webHistoryFileStore.delete(webHistoryFileStore.readAll().first())

        assertTrue(slot.captured.toMutableList().isEmpty())
        verify { Files.write(any(), slot.captured)  }
    }

    @Test
    fun readAll() {
        val readAll = webHistoryFileStore.readAll()

        assertEquals(1, readAll.size)
    }

    @Test
    fun readAllNotExists() {
        every { Files.exists(parent) } returns true
        every { Files.exists(path) } returns false

        val readAll = webHistoryFileStore.readAll()

        assertTrue(readAll.isEmpty())
        verify(exactly = 1) { Files.exists(parent) }
        verify(inverse = true) { Files.createDirectories(any()) }
    }

    @Test
    fun readAllWithCreateFolder() {
        every { Files.exists(parent) } returns false

        val readAll = webHistoryFileStore.readAll()

        assertEquals(1, readAll.size)
        verify(exactly = 1) { Files.exists(parent) }
        verify(exactly = 1) { Files.createDirectories(any()) }
    }

    @Test
    fun readAllWithInsufficientInput() {
        every { Files.exists(parent) } returns false
        every { Files.newInputStream(any()) } returns "Yahoo! JAPAN Test\thttps://www.yahoo.co.jp".byteInputStream()

        val readAll = webHistoryFileStore.readAll()

        assertEquals(1, readAll.size)
        verify(exactly = 1) { Files.exists(parent) }
        verify(exactly = 1) { Files.createDirectories(any()) }
        readAll.first().also {
            assertEquals(0, it.lastVisitedTime)
            assertEquals(0, it.visitingCount)
        }
    }

    @Test
    fun readAllWithInsufficientInputOnlyTitleCase() {
        every { Files.exists(parent) } returns false
        every { Files.newInputStream(any()) } returns "Yahoo! JAPAN Test".byteInputStream()

        val readAll = webHistoryFileStore.readAll()

        assertEquals(1, readAll.size)
        verify(exactly = 1) { Files.exists(parent) }
        verify(exactly = 1) { Files.createDirectories(any()) }
        readAll.first().also {
            assertTrue(it.url.isEmpty())
            assertEquals(0, it.lastVisitedTime)
            assertEquals(0, it.visitingCount)
        }
    }

    @Test
    fun readAllWithContainingBlankLineCase() {
        every { Files.exists(parent) } returns false
        every { Files.newInputStream(any()) } returns listOf(
            "   ",
            "",
            "Yahoo! JAPAN Test\thttps://www.yahoo.co.jp",
            "Yahoo! JAPAN Test2\thttps://www.yahoo.co.jp\t1\t2",
            "Yahoo! JAPAN Test3\thttps://www.yahoo.co.jp\t\t",
            "Yahoo! JAPAN Test4\thttps://www.yahoo.co.jp\t",
        ).joinToString("\n").byteInputStream()

        val readAll = webHistoryFileStore.readAll()

        assertEquals(4, readAll.size)
        verify(exactly = 1) { Files.exists(parent) }
        verify(exactly = 1) { Files.createDirectories(any()) }
        readAll.first().also {
            assertEquals(0, it.lastVisitedTime)
            assertEquals(0, it.visitingCount)
        }
    }

    @Test
    fun clear() {
        every { Files.delete(any()) } just Runs

        webHistoryFileStore.clear()

        verify { Files.delete(any()) }
    }

}