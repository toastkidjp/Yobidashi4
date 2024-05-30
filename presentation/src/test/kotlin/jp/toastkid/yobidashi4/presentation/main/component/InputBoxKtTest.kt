package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InputBoxKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(InputBoxViewModel::class)
        every { anyConstructed<InputBoxViewModel>().showInputBox() } returns true
        every { anyConstructed<InputBoxViewModel>().start() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun inputBox() {
        val text = "test-input"
        every { anyConstructed<InputBoxViewModel>().query() } returns TextFieldValue(text)
        every { anyConstructed<InputBoxViewModel>().invokeAction() } just Runs

        runDesktopComposeUiTest {
            setContent {
                InputBox()
            }

            onNode(hasText(text), useUnmergedTree = true).performImeAction()
            verify { anyConstructed<InputBoxViewModel>().invokeAction() }
        }
    }
}