package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.viewmodel.TextEditorViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class SimpleTextEditorKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        every { mainViewModel.darkMode() } returns false
        every { setting.editorConversionLimit() } returns 3_000

        mockkConstructor(TextEditorViewModel::class)
        every { anyConstructed<TextEditorViewModel>().launchTab(any()) } just Runs
        every { anyConstructed<TextEditorViewModel>().initialScroll(any()) } just Runs
        every { anyConstructed<TextEditorViewModel>().onValueChange(any()) } just Runs
        every { anyConstructed<TextEditorViewModel>().onClickLineNumber(any()) } just Runs
        every { anyConstructed<TextEditorViewModel>().dispose() } just Runs
        coEvery { anyConstructed<TextEditorViewModel>().adjustLineNumberState() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun simpleTextEditor() {
        val setStatus = mockk<(String) -> Unit>()
        every { setStatus(any()) } just Runs
        val textFieldValue = TextFieldValue("test_content\nSecond line\n3rd line")
        every { anyConstructed<TextEditorViewModel>().content() } returns textFieldValue

        runDesktopComposeUiTest {
            setContent {
                SimpleTextEditor(
                    EditorTab(mockk()),
                    setStatus
                )
            }

            onNodeWithContentDescription("Editor input area", useUnmergedTree = true)
                .assertExists("Not exists!")
                .performClick()
                .performKeyInput {
                    pressKey(Key.A, 1000L)
                }
                .performTextInput("test new value")
            verify { setStatus(any()) }
            verify { anyConstructed<TextEditorViewModel>().onValueChange(any()) }

            onNodeWithContentDescription("Line number 1", useUnmergedTree = true)
                .performClick()
            verify { anyConstructed<TextEditorViewModel>().onClickLineNumber(any()) }
        }
    }

}