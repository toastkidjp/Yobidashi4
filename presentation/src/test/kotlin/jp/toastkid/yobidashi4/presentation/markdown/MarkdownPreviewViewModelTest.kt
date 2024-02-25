package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MarkdownPreviewViewModelTest {

    private lateinit var subject: MarkdownPreviewViewModel

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
        every { mainViewModel.webSearch(any()) } just Runs
        every { mainViewModel.selectedText() } returns "test"

        mockkConstructor(KeywordHighlighter::class)
        every { anyConstructed<KeywordHighlighter>().invoke(any(), any()) } returns mockk()

        subject = MarkdownPreviewViewModel(ScrollState(0))
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun onKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                val consumed = subject.onKeyEvent(
                    rememberCoroutineScope(),
                    KeyEvent(
                        java.awt.event.KeyEvent(
                            mockk(),
                            java.awt.event.KeyEvent.KEY_PRESSED,
                            1,
                            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                            java.awt.event.KeyEvent.VK_UP,
                            '-'
                        )
                    )
                )

                assertTrue(consumed)
            }
        }
    }

    @Test
    fun noopOnKeyEventWithWebSearchShortcutWithKeyPressed() {
        val consumed = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.SHIFT_DOWN_MASK,
                    java.awt.event.KeyEvent.VK_O,
                    '-'
                )
            )
        )

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @Test
    fun onKeyEventWithWebSearchShortcut() {
        val consumed = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_RELEASED,
                    1,
                    java.awt.event.KeyEvent.CTRL_DOWN_MASK or java.awt.event.KeyEvent.SHIFT_DOWN_MASK,
                    java.awt.event.KeyEvent.VK_O,
                    '-'
                )
            )
        )

        assertTrue(consumed)
        verify { mainViewModel.webSearch(any()) }
        verify { mainViewModel.selectedText() }
    }

    @Test
    fun annotate() {
        subject.annotate("It longs to ~~make~~ it.", "long")

        verify { anyConstructed<KeywordHighlighter>().invoke(any(), any()) }
    }

}