package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performKeyPress
import androidx.compose.ui.test.runDesktopComposeUiTest
import org.junit.jupiter.api.Test

class ConverterToolTabViewKtTest {

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun converterToolTabView() {
        runDesktopComposeUiTest {
            setContent {
                ConverterToolTabView()
            }

            val node = onNode(hasContentDescription("surface"), useUnmergedTree = true)
            node.performKeyPress(KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true))
            node.performKeyInput {
                keyDown(Key.DirectionDown)
                keyUp(Key.DirectionDown)
            }
        }
    }
}