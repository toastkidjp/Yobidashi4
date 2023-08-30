package jp.toastkid.yobidashi4.domain.service.media

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
import java.util.stream.Stream
import kotlin.io.path.isExecutable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MediaFileFinderTest {

    @InjectMockKs
    private lateinit var mediaFileFinder: MediaFileFinder

    @MockK
    private lateinit var root: Path

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var fileName: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Path::class)
        every { Path.of(any<String>()) } returns root

        mockkStatic(Files::class)
        every { Files.list(path) } returns Stream.of(mockk())
        every { Files.list(root) } answers { Stream.of(path) }
        every { Files.isDirectory(root) } returns true
        every { Files.isDirectory(path) } returns false

        every { path.fileName } returns fileName
        every { path.isExecutable() } returns true
        every { fileName.toString() } returns "media.mp3"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val collection = mediaFileFinder.invoke("test")

        assertEquals(1, collection.size)
        verify { Path.of(any<String>()) }
        verify(inverse = true) { Files.list(path) }
        verify { Files.list(root) }
        verify { Files.isDirectory(root) }
        verify { Files.isDirectory(path) }
        verify { path.fileName }
        verify { path.isExecutable() }
        verify { fileName.toString() }
    }

    @Test
    fun notExists() {
        every { Files.isDirectory(root) } returns false

        assertTrue(mediaFileFinder.invoke("empty").isEmpty())
    }

}