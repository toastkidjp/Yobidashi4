package jp.toastkid.yobidashi4.infrastructure.service.photo.gif

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.source
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage
import java.nio.file.Path

class GifImageReaderTest {

    private lateinit var subject: GifImageReader

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var consumer: (BufferedImage, Int) -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        val fakeFileSystem = FakeFileSystem()
        val fakePath = "test.gif".toPath()
        val byteArray = javaClass.classLoader.getResource("photo/gif/empty.gif").openStream().source().buffer().use { stream ->
            val source = stream.readByteArray()
            source
        }
        fakeFileSystem.write(fakePath) {
            write(byteArray)
        }
        path = fakePath.toNioPath()
        every { consumer.invoke(any(), any()) } just Runs

        subject = GifImageReader(fakeFileSystem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke(path, consumer)

        verify { consumer.invoke(any(), any()) }
    }

}