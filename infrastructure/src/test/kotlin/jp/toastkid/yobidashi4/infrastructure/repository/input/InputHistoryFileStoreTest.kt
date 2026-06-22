package jp.toastkid.yobidashi4.infrastructure.repository.input

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class InputHistoryFileStoreTest {

    private lateinit var subject: InputHistoryFileStore

    @MockK
    private lateinit var path: Path

    private lateinit var fakeFileSystem: FakeFileSystem

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Path::class)
        every { Path.of(any<String>()) }.returns(path)
        every { path.parent }.returns(path)
        every { path.resolve(any<String>()) }.returns(path)

        fakeFileSystem = FakeFileSystem()
        fakeFileSystem.createDirectories("temporary/input/history/".toPath())
        fakeFileSystem.write("temporary/input/history/test.tsv".toPath()) { writeUtf8("test\t$timestamp") }

        subject = InputHistoryFileStore(fakeFileSystem, "test")
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun listEmptyCase() {
        fakeFileSystem.delete("temporary/input/history/test.tsv".toPath())
        fakeFileSystem.delete("temporary/input/history/".toPath())

        assertTrue(subject.list().isEmpty())
    }

    private val timestamp = System.currentTimeMillis()

    @Test
    fun list() {
        val bookmarks = subject.list()
        assertTrue(bookmarks.isNotEmpty())
        assertEquals("test", bookmarks.first().word)
        assertEquals(timestamp, bookmarks.first().timestamp)
    }

    @Test
    fun filter() {
        assertTrue(subject.filter("taken").isEmpty())
    }

    @Test
    fun filterWithNull() {
        assertEquals(1, subject.filter(null).size)
    }

    @Test
    fun filterWithBlank() {
        assertEquals(1, subject.filter(" ").size)
    }

    @Test
    fun filterWithExisting() {
        assertEquals(1, subject.filter("test").size)
    }

    @Test
    fun add() {
        fakeFileSystem.write("temporary/input/history/test.tsv".toPath()) {
            writeUtf8("test\t1\ntest\t3\ntest\t2",)
        }

        subject.add(InputHistory("test", 1))

        fakeFileSystem.source("temporary/input/history/test.tsv".toPath()).buffer().use {
            val content = it.readUtf8()
            assertEquals("test\t3", content)
            assertEquals(1, content.split("\n").size)
        }
    }

    @Test
    fun addNotFoundCase() {
        fakeFileSystem.delete("temporary/input/history/test.tsv".toPath())
        fakeFileSystem.delete("temporary/input/history/".toPath())

        subject.add(InputHistory("test", timestamp))

        fakeFileSystem.source("temporary/input/history/test.tsv".toPath()).buffer().use {
            val content = it.readUtf8()
            assertEquals("test", content.split("\t")[0])
        }
    }

    @Test
    fun delete() {
        val item = InputHistory("test", timestamp)
        subject.delete(item)

        fakeFileSystem.source("temporary/input/history/test.tsv".toPath()).buffer().use {
            val content = it.readUtf8()
            assertTrue(content.isEmpty())
        }
    }

    @Test
    fun deleteWithWord() {
        subject.deleteWithWord("test")

        fakeFileSystem.source("temporary/input/history/test.tsv".toPath()).buffer().use {
            val content = it.readUtf8()
            assertTrue(content.isEmpty())
        }
    }

    @Test
    fun clear() {
        subject.clear()

        assertFalse(fakeFileSystem.exists("temporary/input/history/test.tsv".toPath()))
    }

}