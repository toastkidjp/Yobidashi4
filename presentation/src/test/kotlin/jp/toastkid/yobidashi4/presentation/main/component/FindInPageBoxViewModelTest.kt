package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.event.KeyEvent
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.lib.input.InputHistoryService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class FindInPageBoxViewModelTest {

    private lateinit var subject: FindInPageBoxViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                }
            )
        }

        mockkConstructor(InputHistoryService::class)

        subject = FindInPageBoxViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun onKeyEvent() {
        every { mainViewModel.switchFind() } just Runs
        val keyEvent = androidx.compose.ui.input.key.KeyEvent(
            KeyEvent(
                mockk(),
                KeyEvent.KEY_PRESSED,
                1,
                KeyEvent.ALT_DOWN_MASK,
                KeyEvent.VK_ESCAPE,
                ','
            )
        )

        val consumed = subject.onKeyEvent(keyEvent)

        assertTrue(consumed)
        verify { mainViewModel.switchFind() }
    }

    @Test
    fun notConsumedKeyEvent() {
        val keyEvent = androidx.compose.ui.input.key.KeyEvent(
            KeyEvent(
                mockk(),
                KeyEvent.KEY_PRESSED,
                1,
                KeyEvent.CTRL_DOWN_MASK,
                KeyEvent.VK_COMMA,
                ','
            )
        )

        val consumed = subject.onKeyEvent(keyEvent)

        assertFalse(consumed)
    }

    @Test
    fun notConsumedOnKeyEventWithEscapeReleased() {
        every { mainViewModel.switchFind() } just Runs

        val consumed = subject.onKeyEvent(
            androidx.compose.ui.input.key.KeyEvent(
                KeyEvent(
                    mockk(),
                    KeyEvent.KEY_RELEASED,
                    1,
                    KeyEvent.CTRL_DOWN_MASK,
                    KeyEvent.VK_ESCAPE,
                    'A'
                )
            )
        )

        assertFalse(consumed)
        verify(inverse = true) { mainViewModel.switchFind() }
    }

    @Test
    fun switchFind() {
        every { mainViewModel.switchFind() } just Runs

        subject.switchFind()

        verify { mainViewModel.switchFind() }
    }

    @Test
    fun inputValue() {
        val textFieldValue = mockk<TextFieldValue>()
        every { mainViewModel.inputValue() } returns textFieldValue

        subject.inputValue()

        verify { mainViewModel.inputValue() }
    }

    @Test
    fun onFindInputChange() {
        every { mainViewModel.onFindInputChange(any()) } just Runs
        every { anyConstructed<InputHistoryService>().filter(any(), any()) } returns mockk()

        subject.onFindInputChange(TextFieldValue("test new"))

        verify { mainViewModel.onFindInputChange(any()) }
        verify { anyConstructed<InputHistoryService>().filter(any(), any()) }
    }

    @Test
    fun shouldShowInputHistory() {
        assertFalse(subject.shouldShowInputHistory())
    }

    @Test
    fun inputHistories() {
        assertTrue(subject.inputHistories().isEmpty())
    }

    @Test
    fun onClickInputHistory() {
        every { anyConstructed<InputHistoryService>().make(any()) } returns TextFieldValue("test ")
        every { mainViewModel.onFindInputChange(any()) } just Runs

        subject.onClickInputHistory("test")

        verify { anyConstructed<InputHistoryService>().make(any()) }
        verify { mainViewModel.onFindInputChange(any()) }
    }

    @Test
    fun onClickInputHistoryMakeNullCase() {
        every { anyConstructed<InputHistoryService>().make(any()) } returns null
        every { mainViewModel.onFindInputChange(any()) } just Runs

        subject.onClickInputHistory("test")

        verify { anyConstructed<InputHistoryService>().make(any()) }
        verify(inverse = true) { mainViewModel.onFindInputChange(any()) }
    }

    @Test
    fun onClickDelete() {
        every { anyConstructed<InputHistoryService>().delete(any(), any()) } just Runs

        subject.onClickDelete("test")

        verify { anyConstructed<InputHistoryService>().delete(any(), any()) }
    }

    @Test
    fun onClickClear() {
        every { anyConstructed<InputHistoryService>().clear(any()) } just Runs

        subject.onClickClear()

        verify { anyConstructed<InputHistoryService>().clear(any()) }
    }

    @Test
    fun onFocusChanged() {
        val focusState = mockk<FocusState>()
        every { focusState.hasFocus } returns false

        subject.onFocusChanged(focusState)
    }

    @Test
    fun onFocusChangedWhichStateIsFocused() {
        val focusState = mockk<FocusState>()
        every { focusState.hasFocus } returns true

        subject.onFocusChanged(focusState)
    }

    @Test
    fun focusRequester() {
        val focusRequester = subject.focusRequester()

        assertNotNull(focusRequester)
    }

    @Test
    fun useReplace() {
        every { mainViewModel.currentTab() } returns mockk<EditorTab>()

        val value = subject.useReplace()

        assertTrue(value)
        verify { mainViewModel.currentTab() }
    }

    @Test
    fun useReplaceReturensFalseCase() {
        every { mainViewModel.currentTab() } returns mockk<WebTab>()

        val value = subject.useReplace()

        assertFalse(value)
        verify { mainViewModel.currentTab() }
    }

    @Test
    fun replaceInputValue() {
        every { mainViewModel.replaceInputValue() } returns mockk()

        subject.replaceInputValue()

        verify { mainViewModel.replaceInputValue() }
    }

    @Test
    fun onReplaceInputChange() {
        every { mainViewModel.onReplaceInputChange(any()) } just Runs

        subject.onReplaceInputChange(TextFieldValue("test new"))

        verify { mainViewModel.onReplaceInputChange(any()) }
    }

    @Test
    fun caseSensitive() {
        every { mainViewModel.caseSensitive() } returns true

        val caseSensitive = subject.caseSensitive()

        assertTrue(caseSensitive)
        verify { mainViewModel.caseSensitive() }
    }

    @Test
    fun switchCaseSensitive() {
        every { mainViewModel.switchCaseSensitive() } just Runs

        subject.switchCaseSensitive()

        verify { mainViewModel.switchCaseSensitive() }
    }

    @Test
    fun findUp() {
        every { mainViewModel.findUp() } just Runs

        subject.findUp()

        verify { mainViewModel.findUp() }
    }

    @Test
    fun findDown() {
        every { mainViewModel.findDown() } just Runs

        subject.findDown()

        verify { mainViewModel.findDown() }
    }

    @Test
    fun replaceAll() {
        every { mainViewModel.replaceAll() } just Runs

        subject.replaceAll()

        verify { mainViewModel.replaceAll() }
    }

    @Test
    fun findStatus() {
        every { mainViewModel.findStatus() } returns "test"

        val value = subject.findStatus()

        assertEquals("test", value)
        verify { mainViewModel.findStatus() }
    }

    @Test
    fun openFind() {
        every { mainViewModel.openFind() } returns true

        val value = subject.openFind()

        assertTrue(value)
        verify { mainViewModel.openFind() }
    }

    @Test
    fun launch() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs

        subject.launch()

        verify { focusRequester.requestFocus() }
    }

}