package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
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

            onNodeWithContentDescription("surface", useUnmergedTree = true).performClick()
                .performKeyInput {
                pressKey(Key.DirectionUp, 1000L)
            }
        }
    }
}