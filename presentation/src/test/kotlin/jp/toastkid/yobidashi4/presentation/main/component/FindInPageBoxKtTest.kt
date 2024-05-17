package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class FindInPageBoxKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }

        every { mainViewModel.currentTab() } returns mockk<EditorTab>()
        every { mainViewModel.findStatus() } returns ""
        every { mainViewModel.inputValue() } returns TextFieldValue()
        every { mainViewModel.replaceInputValue() } returns TextFieldValue()
        every { mainViewModel.caseSensitive() } returns false
        every { mainViewModel.openFind() } returns false

        mockkConstructor(FindInPageBoxViewModel::class)
        every { anyConstructed<FindInPageBoxViewModel>().launch() } just Runs
        every { anyConstructed<FindInPageBoxViewModel>().onFindInputChange(any()) } just Runs
        every { anyConstructed<FindInPageBoxViewModel>().findUp() } just Runs
        every { anyConstructed<FindInPageBoxViewModel>().findDown() } just Runs
        every { anyConstructed<FindInPageBoxViewModel>().switchCaseSensitive() } just Runs
        every { anyConstructed<FindInPageBoxViewModel>().switchFind() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun findInPageBox() {
        val text = "input_field"
        every { anyConstructed<FindInPageBoxViewModel>().inputValue() } returns TextFieldValue(text)
        runDesktopComposeUiTest {
            setContent {
                FindInPageBox()
            }

            verify { mainViewModel.currentTab() }
            verify { mainViewModel.findStatus() }
            verify { mainViewModel.openFind() }

            onNode(hasText("x")).performClick()
            verify { anyConstructed<FindInPageBoxViewModel>().switchFind() }

            onNode(hasText("↑")).performClick()
            verify { anyConstructed<FindInPageBoxViewModel>().findUp() }

            onNode(hasText("↓")).performClick()
            verify { anyConstructed<FindInPageBoxViewModel>().findDown() }

            onNodeWithContentDescription("Find input").performKeyInput {
                keyDown(Key.K)
                keyUp(Key.K)
            }.performTextInput("test")
            verify { anyConstructed<FindInPageBoxViewModel>().onFindInputChange(any()) }

            onNodeWithContentDescription("Case sensitive checkbox", useUnmergedTree = true).performClick()
            verify { anyConstructed<FindInPageBoxViewModel>().switchCaseSensitive() }

            onAllNodesWithContentDescription("Clear input.").onFirst().performClick()
            verify { anyConstructed<FindInPageBoxViewModel>().onFindInputChange(any()) }
        }
    }
}