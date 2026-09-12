package jp.toastkid.yobidashi4.presentation.setting

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.withKeyDown
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SettingEditorViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: SettingEditorViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { viewModel.listState() } returns LazyListState()
        every { viewModel.items() } returns listOf(
            "test" to TextFieldState(),
            "test2" to TextFieldState("test2"),
            "test3" to TextFieldState("cursor_target")
        )
        every { viewModel.start() } just Runs
        every { viewModel.save() } just Runs
        every { viewModel.openFile() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingEditorView() {
        runComposeUiTest {
            setContent {
                SettingEditorView(viewModel)
            }

            onNodeWithText("Save").performClick()
            onNodeWithText("Open").performClick()
            onNodeWithText("Open").performKeyInput {
                withKeyDown(Key.CtrlLeft) {
                    keyDown(Key.O)
                }
                keyUp(Key.O)
            }

            val input = onNodeWithText("cursor_target")
            input.performKeyInput {
                keyDown(Key.O)
                keyUp(Key.O)
                pressKey(Key.I)
            }

            val textInput = onNodeWithText("test2")
            textInput.performTextInput("Good")

            val clearInput = onAllNodesWithContentDescription("Clear input.").onFirst()
            clearInput.performClick()
            clearInput.onParent().performMouseInput {
                enter()
                exit()
            }

            verify { viewModel.start() }
            verify { viewModel.save() }
            verify { viewModel.openFile() }
        }
    }
}