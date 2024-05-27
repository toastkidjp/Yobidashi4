package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.font.FontWeight
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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

        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<URL>()) } returns BufferedImage(1, 1, Image.SCALE_FAST)

        subject = MarkdownPreviewViewModel(ScrollState(0))
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                val consumed = subject.onKeyEvent(
                    rememberCoroutineScope(),
                    KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true)
                )

                assertTrue(consumed)
            }
        }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun noopOnKeyEventWithWebSearchShortcutWithKeyPressed() {
        val consumed = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(Key.O, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true)
        )

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun elseCaseOnKeyEventWithWebSearchShortcut() {
        val consumed = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(Key.Q, KeyEventType.KeyUp, isCtrlPressed = true, isShiftPressed = true)
        )

        assertFalse(consumed)
        verify { mainViewModel wasNot called }
    }

    @Test
    fun extractText() {
        assertEquals("test", subject.extractText("test", false))
        assertEquals("- [ ] test", subject.extractText("- [ ] test", false))
        assertEquals(" test", subject.extractText("- [ ] test", true))
    }

    @Test
    fun loadBitmapWithIncorrectUrl() {
        val bitmap = subject.loadBitmap("test")

        assertNull(bitmap)
        verify(inverse = true) { ImageIO.read(any<URL>()) }
    }

    @Test
    fun loadBitmap() {
        val bitmap = subject.loadBitmap("https://test")

        assertNotNull(bitmap)
        verify { ImageIO.read(any<URL>()) }
    }

    @Test
    fun loadBitmapWithExceptionCase() {
        every { ImageIO.read(any<URL>()) } throws mockk<IOException>()

        val bitmap = subject.loadBitmap("https://test")

        assertNull(bitmap)
        verify { ImageIO.read(any<URL>()) }
    }

    @Test
    fun makeFontWeight() {
        assertEquals(FontWeight.Normal, subject.makeFontWeight(-1))
        assertEquals(FontWeight.Bold, subject.makeFontWeight(0))
    }

}