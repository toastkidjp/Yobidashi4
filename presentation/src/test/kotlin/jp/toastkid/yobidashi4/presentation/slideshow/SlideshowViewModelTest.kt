package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.test.ExperimentalTestApi
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.presentation.slideshow.lib.ImageCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.Image
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.IIOException
import javax.imageio.ImageIO

class SlideshowViewModelTest {

    private lateinit var subject: SlideshowViewModel

    @MockK
    private lateinit var onEscapeKeyReleased: () -> Unit

    @MockK
    private lateinit var onFullscreenKeyReleased: () -> Unit

    @MockK
    private lateinit var pagerState: PagerState

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { onEscapeKeyReleased.invoke() } just Runs
        every { onFullscreenKeyReleased.invoke() } just Runs
        coEvery { pagerState.scrollToPage(any()) } just Runs
        every { pagerState.currentPage } returns 1
        mockkConstructor(ImageCache::class)
        every { anyConstructed<ImageCache>().get(any()) } returns mockk()
        every { anyConstructed<ImageCache>().put(any(), any()) } just Runs

        subject = SlideshowViewModel()

        val deck = mockk<SlideDeck>()
        every { deck.slides } returns mutableListOf(mockk(), mockk())
        every { deck.extractImageUrls() } returns setOf("a", "b", "c")
        subject.launch(deck, onEscapeKeyReleased, onFullscreenKeyReleased)

        verify(exactly = 3) { anyConstructed<ImageCache>().get(any()) }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventLeft() = runTest {
        val job = launch(Dispatchers.Unconfined) {
            subject.scrollEventFlow().collect {
                assertEquals(0, it)
            }
        }

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.DirectionLeft, KeyEventType.KeyUp, isCtrlPressed = true),
            pagerState
        )
        job.cancelAndJoin()
        assertTrue(consumed)
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventRight() = runTest {
        val job = launch(Dispatchers.Unconfined) {
            subject.scrollEventFlow().collect {
                assertEquals(1, it)
            }
        }

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.DirectionRight, KeyEventType.KeyUp, isCtrlPressed = true),
            pagerState
        )
        job.cancelAndJoin()
        assertTrue(consumed)
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventEnter() = runTest {
        val job = launch(Dispatchers.Unconfined) {
            subject.scrollEventFlow().collect {
                assertEquals(1, it)
            }
        }

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Enter, KeyEventType.KeyUp, isCtrlPressed = true),
            pagerState
        )
        job.cancelAndJoin()
        assertTrue(consumed)
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventF5() {
        val consumed = subject.onKeyEvent(
            KeyEvent(Key.F5, KeyEventType.KeyUp, isCtrlPressed = true),
            pagerState
        )
        assertTrue(consumed)
        verify { onFullscreenKeyReleased.invoke() }
    }


    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventF5ForCoverage() {
        subject = SlideshowViewModel()

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.F5, KeyEventType.KeyUp, isCtrlPressed = true),
            pagerState
        )
        assertTrue(consumed)
        verify { onFullscreenKeyReleased wasNot called }
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onNoopKeyEvent() {
        val consumed = subject.onKeyEvent(
            KeyEvent(Key.F7, KeyEventType.KeyUp, isCtrlPressed = true),
            pagerState
        )
        assertFalse(consumed)
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventEscape() {
        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Escape, KeyEventType.KeyUp, isCtrlPressed = true),
            pagerState
        )

        assertTrue(consumed)
        verify { onEscapeKeyReleased.invoke() }
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventEscapeForCoverage() {
        subject = SlideshowViewModel()

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Escape, KeyEventType.KeyUp, isCtrlPressed = true),
            pagerState
        )

        assertTrue(consumed)
        verify { onEscapeKeyReleased wasNot called }
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun otherKeyEvent() {
        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Escape, KeyEventType.KeyDown, isCtrlPressed = true),
            pagerState
        )

        assertFalse(consumed)
    }

    @Test
    fun loadImage() {
        subject.loadImage("test")

        verify(inverse = true) { anyConstructed<ImageCache>().put(any(), any()) }
    }

    @Test
    fun loadImageNotHitCase() {
        every { anyConstructed<ImageCache>().get(any()) } returns null
        val backgroundUrl = javaClass.classLoader.getResource("icon/icon.png")?.toString() ?: return fail("Resource is not found.")
        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<URL>()) } returns BufferedImage(1, 1, Image.SCALE_FAST)

        subject.loadImage(backgroundUrl)

        verify { ImageIO.read(any<URL>()) }
        verify { anyConstructed<ImageCache>().put(any(), any()) }
    }

    @Test
    fun loadImageThrowIIOExceptionCase() {
        every { anyConstructed<ImageCache>().get(any()) } returns null
        val backgroundUrl = javaClass.classLoader.getResource("icon/icon.png")?.toString()
            ?: return fail("Resource is not found.")
        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<URL>()) } throws IIOException("Test")

        subject.loadImage(backgroundUrl)

        verify { ImageIO.read(any<URL>()) }
        verify(inverse = true) { anyConstructed<ImageCache>().put(any(), any()) }
    }

    @Test
    fun sliderValue() {
        assertEquals(0f, subject.sliderValue())

        subject.setSliderValue(1f)

        assertEquals(1f, subject.sliderValue())
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun requestFocus() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } returns true

        subject.requestFocus()

        verify { subject.focusRequester() }
        verify { focusRequester.requestFocus() }
    }

}