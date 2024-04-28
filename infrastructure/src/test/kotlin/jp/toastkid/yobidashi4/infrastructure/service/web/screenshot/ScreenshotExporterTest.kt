package jp.toastkid.yobidashi4.infrastructure.service.web.screenshot

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Component
import java.awt.Container
import java.awt.Rectangle
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScreenshotExporterTest {

    @InjectMockKs
    private lateinit var subject: ScreenshotExporter

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var component: Component

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Path::class, Files::class, ImageIO::class)
        every { Path.of(any<String>()) } returns path
        every { path.resolve(any<String>()) } returns path
        every { Files.exists(any()) } returns true
        every { Files.createDirectories(any()) } returns mockk()
        every { Files.newOutputStream(any()) } returns ByteArrayOutputStream()
        every { ImageIO.write(any(), any(), any<OutputStream>()) } returns true
        every { component.bounds } returns Rectangle(1, 1)
        every { component.parent } returns Container()
        every { component.x } returns 1
        every { component.y } returns 1
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke(component)

        verify(inverse = true) { Files.createDirectories(any()) }
        verify { ImageIO.write(any(), any(), any<OutputStream>()) }
    }

    @Test
    fun makeDirectoriesIfNeed() {
        every { Files.exists(any()) } returns false
        every { component.x } returns 0

        subject.invoke(component)

        verify(exactly = 1) { Files.createDirectories(any()) }
        verify(inverse = true) { ImageIO.write(any(), any(), any<OutputStream>()) }
    }

    @Test
    fun noopWhenComponentYIsZero() {
        every { Files.exists(any()) } returns false
        every { component.y } returns 0

        subject.invoke(component)

        verify(exactly = 1) { Files.createDirectories(any()) }
        verify(inverse = true) { ImageIO.write(any(), any(), any<OutputStream>()) }
    }

}