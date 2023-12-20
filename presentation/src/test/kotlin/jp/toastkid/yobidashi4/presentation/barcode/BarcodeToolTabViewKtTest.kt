package jp.toastkid.yobidashi4.presentation.barcode

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeDecoder
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeEncoder
import jp.toastkid.yobidashi4.presentation.viewmodel.barcode.BarcodeToolTabViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class BarcodeToolTabViewKtTest {

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
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { barcodeEncoder } bind (BarcodeEncoder::class)
                    single(qualifier = null) { barcodeDecoder } bind (BarcodeDecoder::class)
                }
            )
        }

        mockkConstructor(BarcodeToolTabViewModel::class)
        every { anyConstructed<BarcodeToolTabViewModel>().barcodeImage() } returns ImageBitmap(1, 1)
        every { anyConstructed<BarcodeToolTabViewModel>().onClickImage() } just Runs
        every { anyConstructed<BarcodeToolTabViewModel>().onClickDecodeResult() } just Runs
        every { anyConstructed<BarcodeToolTabViewModel>().encodeInputValue() } returns TextFieldValue()
        every { anyConstructed<BarcodeToolTabViewModel>().setEncodeInputValue(any()) } just Runs
        every { anyConstructed<BarcodeToolTabViewModel>().decodeInputValue() } returns TextFieldValue()
        every { anyConstructed<BarcodeToolTabViewModel>().setDecodeInputValue(any()) } just Runs
        every { anyConstructed<BarcodeToolTabViewModel>().decodeResult() } returns "test"
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun barcodeToolTabView() {
        runDesktopComposeUiTest {
            setContent {
                BarcodeToolTabView()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun nullImage() {
        every { anyConstructed<BarcodeToolTabViewModel>().barcodeImage() } returns null

        runDesktopComposeUiTest {
            setContent {
                BarcodeToolTabView()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun decodeResultBlankCase() {
        every { anyConstructed<BarcodeToolTabViewModel>().decodeResult() } returns "  "

        runDesktopComposeUiTest {
            setContent {
                BarcodeToolTabView()
            }
        }
    }

}