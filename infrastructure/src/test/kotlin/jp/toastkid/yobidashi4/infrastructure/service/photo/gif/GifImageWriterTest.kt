package jp.toastkid.yobidashi4.infrastructure.service.photo.gif

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Path
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GifImageWriterTest {

    private lateinit var subject: GifImageWriter

    @BeforeEach
    fun setUp() {
        subject = GifImageWriter()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val outputTo = mockk<Path>()
        every { outputTo.toFile() } returns File.createTempFile("test", "png")

        subject.invoke(BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), outputTo)

        verify { outputTo.toFile() }
    }

}
