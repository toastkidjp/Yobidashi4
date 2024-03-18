package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import org.junit.jupiter.api.Test

class InputTextFieldKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun inputTextField() {
        runDesktopComposeUiTest {
            setContent {
                InputTextField(
                    TextFieldValue("test"),
                    "label",
                    {},
                    {},
                    {},
                    true,
                    listOf("test"),
                    {},
                    {},
                    {},
                    Modifier
                )
            }
        }
    }
}