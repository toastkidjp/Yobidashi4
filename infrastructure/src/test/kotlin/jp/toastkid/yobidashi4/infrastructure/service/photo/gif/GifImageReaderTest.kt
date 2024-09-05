package jp.toastkid.yobidashi4.infrastructure.service.photo.gif

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.image.BufferedImage
import java.nio.file.Path
import kotlin.io.path.toPath
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GifImageReaderTest {

    private lateinit var subject: GifImageReader

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var consumer: (BufferedImage, Int) -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { path.toFile() } returns javaClass.classLoader.getResource("photo/gif/empty.gif").toURI().toPath().toFile()
        every { consumer.invoke(any(), any()) } just Runs

        subject = GifImageReader()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke(path, consumer)

        verify { path.toFile() }
    }

}