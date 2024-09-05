package jp.toastkid.yobidashi4.infrastructure.service.photo.gif

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.photo.gif.GifDivider
import kotlin.io.path.nameWithoutExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GifDividerImplementationTest {

    private lateinit var subject: GifDivider

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkConstructor(GifImageReader::class)
        every { anyConstructed<GifImageReader>().invoke(any(), any()) } just Runs

        mockkConstructor(GifImageWriter::class)
        every { anyConstructed<GifImageWriter>().invoke(any(), any()) } just Runs

        mockkStatic(Files::class)
        every { Files.createDirectory(any()) } returns mockk()

        val fileName = mockk<Path>()
        every { fileName.nameWithoutExtension } returns "test"
        every { path.fileName } returns fileName
        every { path.toFile() } returns File.createTempFile("test", "png")
        val folder = mockk<Path>()
        every { path.resolveSibling(any<String>()) } returns folder
        every { folder.resolve(any<String>()) } returns Files.createTempFile("test", "png")

        subject = GifDividerImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val capturingSlot = slot<(BufferedImage, Int) -> Unit>()
        every { anyConstructed<GifImageReader>().invoke(any(), capture(capturingSlot)) } just Runs
        val bufferedImage = mockk<BufferedImage>()
        every { bufferedImage.flush() } just Runs

        runBlocking {
            subject.invoke(path)
        }
        capturingSlot.captured.invoke(bufferedImage, 0)

        verify { anyConstructed<GifImageReader>().invoke(any(), any()) }
        verify { anyConstructed<GifImageWriter>().invoke(any(), any()) }
        verify { bufferedImage.flush() }
    }

    @Test
    fun ioException() {
        every { Files.createDirectory(any()) } throws IOException()

        runBlocking {
            subject.invoke(path)
        }

        verify(inverse = true) { anyConstructed<GifImageReader>().invoke(any(), any()) }
        verify(inverse = true) { anyConstructed<GifImageWriter>().invoke(any(), any()) }
    }

}