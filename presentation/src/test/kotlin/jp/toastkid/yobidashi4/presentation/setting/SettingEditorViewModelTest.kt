package jp.toastkid.yobidashi4.presentation.setting

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

class SettingEditorViewModelTest {

    private lateinit var subject: SettingEditorViewModel

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { viewModel } bind(MainViewModel::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        subject = SettingEditorViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        subject.onKeyEvent(CoroutineScope(Dispatchers.Unconfined), KeyEvent(Key.AppSwitch, KeyEventType.KeyDown))
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventWithO() {
        subject = spyk(subject)
        every { subject.openFile() } just Runs

        val onKeyEvent = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertTrue(onKeyEvent)
        verify { subject.openFile() }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventWithOAndWithoutCtrl() {
        subject = spyk(subject)
        every { subject.openFile() } just Runs

        val onKeyEvent = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = false)
        )

        assertFalse(onKeyEvent)
        verify(inverse = true) { subject.openFile() }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun noopOnKeyEventWithKeyUp() {
        subject = spyk(subject)
        every { subject.openFile() } just Runs

        val onKeyEvent = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(Key.O, KeyEventType.KeyUp, isCtrlPressed = true)
        )

        assertFalse(onKeyEvent)
        verify(inverse = true) { subject.openFile() }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventWithS() {
        subject = spyk(subject)
        every { subject.save() } just Runs

        val onKeyEvent = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(Key.S, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertTrue(onKeyEvent)
        verify { subject.save() }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventWithSAndWithoutCtrl() {
        subject = spyk(subject)
        every { subject.save() } just Runs

        val onKeyEvent = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(Key.S, KeyEventType.KeyDown, isCtrlPressed = false)
        )

        assertFalse(onKeyEvent)
        verify(inverse = true) { subject.save() }
    }

    @Test
    fun listState() {
        assertNotNull(subject.listState())
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun start() {
        every { setting.items() } returns mapOf("test" to "value")

        subject.start()

        verify { setting.items() }
        assertEquals(1, subject.items().size)
    }

    @Test
    fun update() {
        every { setting.items() } returns mapOf("test" to "value")
        subject.start()

        subject.update("a")
        subject.update("test")

        assertEquals("updated", subject.items().first().second.text)
    }

    @Test
    fun openFile() {
        every { viewModel.openFile(any()) } just Runs

        subject.openFile()

        verify { viewModel.openFile(any()) }
    }

    @Test
    fun save() {
        every { setting.items() } returns mapOf(
            "test" to "value",
            "empty" to "y"
        )
        subject.start()
        subject.update("empty")
        every { setting.update(any(), any()) } just Runs
        every { setting.save() } just Runs

        subject.save()

        verify { setting.update(any(), any()) }
        verify { setting.save() }
    }

}