package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class InputTextFieldKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun inputTextField() {
        val suggestionConsumer = mockk<(String) -> Unit>()
        every { suggestionConsumer.invoke(any()) } just Runs
        val onClickClear = mockk<() -> Unit>()
        every { onClickClear.invoke() } just Runs
        val onClickDelete = mockk<(String) -> Unit>()
        every { onClickDelete.invoke(any()) } just Runs

        runDesktopComposeUiTest {
            setContent {
                InputTextField(
                    TextFieldValue("test"),
                    "label",
                    {},
                    {},
                    {},
                    true,
                    listOf("suggestion_item"),
                    suggestionConsumer,
                    onClickDelete,
                    onClickClear,
                    {},
                    Modifier
                )
            }

            onNode(hasText("x"), useUnmergedTree = true).performClick()
            verify { onClickDelete.invoke(any()) }

            onNode(hasText("suggestion_item"), useUnmergedTree = true).onParent().performClick()
            verify { suggestionConsumer.invoke(any()) }

            onNode(hasText("Clear history"), useUnmergedTree = true).onParent().performClick()
            verify { onClickClear.invoke() }

            onNodeWithContentDescription("Clear input.").performClick()
        }
    }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun closeSuggestionCase() {
        val clearButton = mockk<() -> Unit>()
        every { clearButton.invoke() } just Runs

        runDesktopComposeUiTest {
            setContent {
                InputTextField(
                    TextFieldValue("test"),
                    "label",
                    {},
                    {},
                    clearButton,
                    false,
                    listOf("test"),
                    {},
                    {},
                    {},
                    {},
                    Modifier
                )
            }

            onNodeWithContentDescription("Clear input.").performClick()
            verify { clearButton.invoke() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun singleLineTextField() {
        val onClearInput = mockk<() -> Unit>()
        every { onClearInput.invoke() } just Runs

        runDesktopComposeUiTest {
            setContent {
                SingleLineTextField(
                    TextFieldValue("test"),
                    "label",
                    {},
                    onClearInput
                )
            }

            onNodeWithContentDescription("Clear input.", useUnmergedTree = true).performClick()
            verify { onClearInput.invoke() }
        }
    }

}