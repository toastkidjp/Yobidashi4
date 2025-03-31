package jp.toastkid.yobidashi4.presentation.setting

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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

    @Test
    fun onKeyEvent() {
        subject.onKeyEvent(CoroutineScope(Dispatchers.Unconfined), KeyEvent(Key.PageUp))
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

        subject.update("a", TextFieldValue("unused"))
        subject.update("test", TextFieldValue("updated"))

        assertEquals("updated", subject.items().first().second.text)
    }

    @Test
    fun openFile() {
        every { viewModel.openFile(any()) } just Runs

        subject.openFile()

        verify { viewModel.openFile(any()) }
    }

}