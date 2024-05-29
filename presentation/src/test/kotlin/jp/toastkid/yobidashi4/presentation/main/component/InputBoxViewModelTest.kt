package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class InputBoxViewModelTest {

    private lateinit var subject: InputBoxViewModel

    @MockK
    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                }
            )
        }

        subject = InputBoxViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun noopInvokeActionWhenQueryIsEmpty() {
        subject.invokeAction()

        verify { viewModel wasNot called }
    }

    @Test
    fun invokeAction() {
        every { viewModel.invokeInputAction(any()) } just Runs
        every { viewModel.setShowInputBox() } just Runs
        subject.onValueChange(TextFieldValue("test"))

        subject.invokeAction()

        verify { viewModel.invokeInputAction(any()) }
        verify { viewModel.setShowInputBox() }
    }

    @Test
    fun onValueChange() {
        subject.onValueChange(TextFieldValue("test"))

        assertEquals("test", subject.query().text)

        subject.clearInput()

        assertTrue(subject.query().text.isEmpty())
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        every { viewModel.setShowInputBox(any()) } just Runs

        val consumed = subject.onKeyEvent(KeyEvent(Key.Escape, KeyEventType.KeyDown))

        assertTrue(consumed)
        verify { viewModel.setShowInputBox(any()) }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun notConsumedOnKeyEvent() {
        every { viewModel.setShowInputBox(any()) } just Runs

        val consumed = subject.onKeyEvent(KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true))

        assertFalse(consumed)
        verify { viewModel wasNot called }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun notConsumedOnKeyEventWithEscapeReleased() {
        every { viewModel.setShowWebSearch(any()) } just Runs

        val consumed = subject.onKeyEvent(KeyEvent(Key.Escape, KeyEventType.KeyUp, isCtrlPressed = true))

        assertFalse(consumed)
        verify { viewModel wasNot called }
    }

    @Test
    fun start() {
        every { viewModel.showInputBox() } returns true
        val tab = mockk<WebTab>()
        every { viewModel.currentTab() } returns tab
        val url = "https://www.yahoo.co.jp"
        every { tab.url() } returns url
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs

        subject.start()

        verify { focusRequester.requestFocus() }
        assertEquals(url, subject.query().text)
    }

    @Test
    fun startIfBoxIsClosed() {
        every { viewModel.showInputBox() } returns false
        every { viewModel.currentTab() } returns mockk()
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs

        subject.start()

        verify { focusRequester wasNot called }
        assertTrue(subject.query().text.isEmpty())
    }

}