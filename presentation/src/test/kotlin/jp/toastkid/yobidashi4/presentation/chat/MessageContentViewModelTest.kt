package jp.toastkid.yobidashi4.presentation.chat

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.util.Base64
import javax.imageio.ImageIO

class MessageContentViewModelTest {

    private lateinit var subject: MessageContentViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var decoder: Base64.Decoder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                }
            )
        }

        mockkStatic(Base64::class)
        every { Base64.getDecoder() } returns decoder
        every { decoder.decode(any<String>()) } returns "test-image-base64".toByteArray()
        mockkStatic(ImageIO::class)
        val image = mockk<BufferedImage>()
        every { ImageIO.read(any<InputStream>()) } returns image
        every { image.width } returns 1
        every { image.height } returns 1
        every { image.getRGB(any(), any()) } returns 1

        subject = MessageContentViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun lineText() {
        assertEquals("- Text", subject.lineText(false, "- Text").text)
        assertEquals("Text", subject.lineText(true, "- Text").text)
    }

    @Test
    fun image() {
        val image = subject.image("test")

        val image2 = subject.image("test")

        assertSame(image, image2)
    }

    @Test
    fun storeImage() {
        mockkStatic(Path::class)
        val path = mockk<Path>()
        every { Path.of(any<String>()) } returns path
        every { path.toFile() } returns mockk()
        every { ImageIO.write(any(), any(), any<File>()) } returns true
        val capturingSlot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(capturingSlot)) } just Runs
        every { mainViewModel.openFile(any()) } just Runs

        subject.storeImage("test")
        capturingSlot.captured.invoke()

        verify { ImageIO.write(any(), any(), any<File>()) }
        verify { mainViewModel.showSnackbar(any(), any(), any()) }
        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun contextMenuState() {
        assertNotNull(subject.contextMenuState())
    }

}
