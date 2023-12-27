package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import org.junit.jupiter.api.Test

class ConverterToolTabViewKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun converterToolTabView() {
        runDesktopComposeUiTest {
            setContent {
                ConverterToolTabView()
            }
        }
    }
}