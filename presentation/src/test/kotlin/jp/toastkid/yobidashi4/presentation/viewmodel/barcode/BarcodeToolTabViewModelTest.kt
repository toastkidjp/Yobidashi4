package jp.toastkid.yobidashi4.presentation.viewmodel.barcode

import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Image
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeDecoder
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeEncoder
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class BarcodeToolTabViewModelTest {

    private lateinit var barcodeToolTabViewModel: BarcodeToolTabViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var barcodeEncoder: BarcodeEncoder

    @MockK
    private lateinit var barcodeDecoder: BarcodeDecoder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { barcodeEncoder } bind(BarcodeEncoder::class)
                    single(qualifier=null) { barcodeDecoder } bind(BarcodeDecoder::class)
                }
            )
        }

        every { mainViewModel.showSnackbar(any()) } just Runs
        every { mainViewModel.openUrl(any(), any()) } just Runs
        every { barcodeEncoder.invoke(any(), any(), any()) } returns mockk()
        every { barcodeDecoder.invoke(any()) } returns "https://www.yahoo.co.jp"

        barcodeToolTabViewModel = BarcodeToolTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun barcodeImage() {
        assertNull(barcodeToolTabViewModel.barcodeImage())
    }

    @Test
    fun setEncodeInputValue() {
        assertTrue(barcodeToolTabViewModel.encodeInputValue().text.isEmpty())

        barcodeToolTabViewModel.setEncodeInputValue(TextFieldValue("test"))

        assertEquals("test", barcodeToolTabViewModel.encodeInputValue().text)
        verify { barcodeEncoder.invoke(any(), any(), any()) }
    }

    @Test
    fun setDecodeInputValue() {
        assertTrue(barcodeToolTabViewModel.decodeInputValue().text.isEmpty())

        barcodeToolTabViewModel.setDecodeInputValue(TextFieldValue("test"))

        assertEquals("test", barcodeToolTabViewModel.decodeInputValue().text)
        verify { barcodeDecoder wasNot called }
    }

    @Test
    fun setDecodeInputValueIfDecoderReturnsNull() {
        every { barcodeDecoder.invoke(any()) } returns null
        mockkStatic(ImageIO::class)
        val image = mockk<BufferedImage>()
        every { ImageIO.read(any<URL>()) } returns image
        every { image.width } returns 1
        every { image.height } returns 1
        every { image.getRGB(any(), any()) } returns 1

        assertTrue(barcodeToolTabViewModel.decodeInputValue().text.isEmpty())

        barcodeToolTabViewModel.setDecodeInputValue(TextFieldValue("https://www.yahoo.co.jp"))

        verify { barcodeDecoder.invoke(any()) }
        assertEquals("https://www.yahoo.co.jp", barcodeToolTabViewModel.decodeInputValue().text)
        assertNotNull(barcodeToolTabViewModel.barcodeImage())
        assertTrue(barcodeToolTabViewModel.decodeResult().isEmpty())
    }

    @Test
    fun setDecodeInputValueIfReadImageSuccessful() {
        mockkStatic(ImageIO::class)
        val image = mockk<BufferedImage>()
        every { ImageIO.read(any<URL>()) } returns image
        every { image.width } returns 1
        every { image.height } returns 1
        every { image.getRGB(any(), any()) } returns 1

        assertTrue(barcodeToolTabViewModel.decodeInputValue().text.isEmpty())

        barcodeToolTabViewModel.setDecodeInputValue(TextFieldValue("https://www.yahoo.co.jp"))

        verify { barcodeDecoder.invoke(any()) }
        assertEquals("https://www.yahoo.co.jp", barcodeToolTabViewModel.decodeInputValue().text)
        assertNotNull(barcodeToolTabViewModel.barcodeImage())
    }

    @Test
    fun decodeResult() {
        assertTrue(barcodeToolTabViewModel.decodeResult().isEmpty())
    }

    @Test
    fun onClickDecodeResult() {
        barcodeToolTabViewModel.onClickDecodeResult()

        verify { mainViewModel.openUrl(any(), any()) }
    }

    @Test
    fun onClickImage() {
        barcodeToolTabViewModel.onClickImage()

        verify(inverse = true) { mainViewModel.showSnackbar(any()) }
    }

    @Test
    fun onClickImageOnSetImage() {
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<Image>()) } just Runs
        barcodeToolTabViewModel.setEncodeInputValue(TextFieldValue("https://www.yahoo.co.jp"))

        barcodeToolTabViewModel.onClickImage()

        verify { mainViewModel.showSnackbar(any()) }
        verify { anyConstructed<ClipboardPutterService>().invoke(any<Image>())  }
    }

}