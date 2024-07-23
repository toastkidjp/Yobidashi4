package jp.toastkid.yobidashi4.presentation.tool.roulette

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RouletteToolTabViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(RouletteToolTabViewModel::class)

        every { anyConstructed<RouletteToolTabViewModel>().input() } returns TextFieldValue()
        every { anyConstructed<RouletteToolTabViewModel>().result() } returns "test"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runDesktopComposeUiTest {
            setContent {
                RouletteToolTabView()
            }
        }
    }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun resultIsEmptyCase() {
        runDesktopComposeUiTest {
            setContent {
                every { anyConstructed<RouletteToolTabViewModel>().result() } returns ""

                RouletteToolTabView()
            }
        }
    }

}