package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
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
import java.net.URL
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.presentation.slideshow.lib.ImageCache
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideshowViewModelTest {

    private lateinit var subject: SlideshowViewModel

    @MockK
    private lateinit var onEscapeKeyReleased: () -> Unit

    @MockK
    private lateinit var onFullscreenKeyReleased: () -> Unit

    @OptIn(ExperimentalFoundationApi::class)
    @MockK
    private lateinit var pagerState: PagerState

    @OptIn(ExperimentalFoundationApi::class)
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { onEscapeKeyReleased.invoke() } just Runs
        every { onFullscreenKeyReleased.invoke() } just Runs
        coEvery { pagerState.animateScrollToPage(any()) } just Runs
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

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventLeft() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.DirectionLeft, KeyEventType.KeyUp, isCtrlPressed = true),
                    pagerState
                )

                coVerify { pagerState.animateScrollToPage(any()) }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventRight() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.DirectionRight, KeyEventType.KeyUp, isCtrlPressed = true),
                    pagerState
                )

                coVerify { pagerState.animateScrollToPage(any()) }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventF5() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.F5, KeyEventType.KeyUp, isCtrlPressed = true),
                    mockk()
                )

                verify { onFullscreenKeyReleased.invoke() }
            }
        }
    }


    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventF5ForCoverage() {
        subject = SlideshowViewModel()

        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.F5, KeyEventType.KeyUp, isCtrlPressed = true),
                    mockk()
                )

                verify { onFullscreenKeyReleased wasNot called }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onNoopKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                val consumed = subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.F7, KeyEventType.KeyUp, isCtrlPressed = true),
                    mockk()
                )

                assertFalse(consumed)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventEscape() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.Escape, KeyEventType.KeyUp, isCtrlPressed = true),
                    mockk()
                )

                verify { onEscapeKeyReleased.invoke() }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventEscapeForCoverage() {
        subject = SlideshowViewModel()

        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.Escape, KeyEventType.KeyUp, isCtrlPressed = true),
                    mockk()
                )

                verify { onEscapeKeyReleased wasNot called }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun otherKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                val consumed = subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.Escape, KeyEventType.KeyDown, isCtrlPressed = true),
                    mockk()
                )

                assertFalse(consumed)
            }
        }
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
    fun sliderVisibility() {
        assertEquals(0f, subject.sliderAlpha())

        subject.showSlider()

        assertEquals(1f, subject.sliderAlpha())

        subject.hideSlider()

        assertEquals(0f, subject.sliderAlpha())
    }
}