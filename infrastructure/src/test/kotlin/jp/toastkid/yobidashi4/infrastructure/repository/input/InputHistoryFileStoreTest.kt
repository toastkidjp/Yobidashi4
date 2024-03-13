package jp.toastkid.yobidashi4.infrastructure.repository.input

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InputHistoryFileStoreTest {

    private lateinit var subject: InputHistoryFileStore

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Files::class)
        every { Files.exists(any()) }.returns(true)
        every { Files.readAllLines(any()) }.returns(listOf("test\t$timestamp"))
        every { Files.write(any(), any<Iterable<String>>()) }.returns(mockk())
        every { Files.createDirectories(any()) }.returns(mockk())

        mockkStatic(Path::class)
        every { Path.of(any<String>()) }.returns(path)
        every { path.parent }.returns(path)
        every { path.resolve(any<String>()) }.returns(path)

        subject = InputHistoryFileStore("test")
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun listEmptyCase() {
        every { Files.exists(any()) }.returns(false)

        Assertions.assertTrue(subject.list().isEmpty())
    }

    private val timestamp = System.currentTimeMillis()

    @Test
    fun list() {
        val bookmarks = subject.list()
        Assertions.assertTrue(bookmarks.isNotEmpty())
        Assertions.assertEquals("test", bookmarks.first().word)
        Assertions.assertEquals(timestamp, bookmarks.first().timestamp)
    }

    @Test
    fun add() {
        subject.add(InputHistory("test", timestamp))

        verify(inverse = true) { Files.createDirectories(any()) }
        verify { Files.write(any(), any<Iterable<String>>()) }
    }

    @Test
    fun addNotFoundCase() {
        every { Files.exists(any()) }.returns(false)

        subject.add(InputHistory("test", timestamp))

        verify { Files.createDirectories(any()) }
        verify { Files.write(any(), any<Iterable<String>>()) }
    }

    @Test
    fun delete() {
        val item = InputHistory("test", timestamp)
        subject.delete(item)

        verify(inverse = true) { Files.createDirectories(any()) }
        verify { Files.write(any(), any<Iterable<String>>()) }
    }

}