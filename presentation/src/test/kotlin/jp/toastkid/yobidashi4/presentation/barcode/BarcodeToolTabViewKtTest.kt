package jp.toastkid.yobidashi4.presentation.barcode

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BarcodeToolTabViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: BarcodeToolTabViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { viewModel.barcodeImage() } returns ImageBitmap(1, 1)
        every { viewModel.onClickImage() } just Runs
        every { viewModel.onClickDecodeResult() } just Runs
        every { viewModel.encodeInputValue() } returns TextFieldState()
        every { viewModel.setEncodeInputValue() } just Runs
        every { viewModel.decodeInputValue() } returns TextFieldState()
        every { viewModel.setDecodeInputValue() } just Runs
        every { viewModel.decodeResult() } returns "test"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun barcodeToolTabView() {
        runDesktopComposeUiTest {
            setContent {
                BarcodeToolTabView(viewModel)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun nullImage() {
        every { viewModel.barcodeImage() } returns null

        runDesktopComposeUiTest {
            setContent {
                BarcodeToolTabView(viewModel)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun decodeResultBlankCase() {
        every { viewModel.decodeResult() } returns "  "

        runDesktopComposeUiTest {
            setContent {
                BarcodeToolTabView(viewModel)
            }
        }
    }

}