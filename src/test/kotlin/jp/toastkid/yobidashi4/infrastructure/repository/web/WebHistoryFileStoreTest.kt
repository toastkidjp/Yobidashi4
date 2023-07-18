package jp.toastkid.yobidashi4.infrastructure.repository.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import org.junit.jupiter.api.AfterEach
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
        every { Files.readAllLines(any()) } returns listOf("Yahoo! JAPAN Test\thttps://www.yahoo.co.jp\t\t")
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun add() {
    }

    @Test
    fun delete() {
        //webHistoryFileStore.delete("test", "https://www.yahoo.co.jp")
        /*
         Files.write(
            getPath(),
            readAll().dropWhile { it.title == title && it.url == url }.map { it.toTsv() }
        )
         */
    }

    @Test
    fun readAll() {
        val readAll = webHistoryFileStore.readAll()

        assertEquals(1, readAll.size)
    }

    @Test
    fun clear() {
        every { Files.delete(any()) } just Runs

        webHistoryFileStore.clear()

        verify { Files.delete(any()) }
    }
}