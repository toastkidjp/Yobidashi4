package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
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

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun onKeyEventLeft() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(
                        java.awt.event.KeyEvent(
                            mockk(),
                            java.awt.event.KeyEvent.KEY_RELEASED,
                            1,
                            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                            java.awt.event.KeyEvent.VK_LEFT,
                            'A'
                        )
                    ),
                    pagerState
                )

                coVerify { pagerState.animateScrollToPage(any()) }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun onKeyEventRight() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(
                        java.awt.event.KeyEvent(
                            mockk(),
                            java.awt.event.KeyEvent.KEY_RELEASED,
                            1,
                            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                            java.awt.event.KeyEvent.VK_RIGHT,
                            'A'
                        )
                    ),
                    pagerState
                )

                coVerify { pagerState.animateScrollToPage(any()) }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun onKeyEventF5() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(
                        java.awt.event.KeyEvent(
                            mockk(),
                            java.awt.event.KeyEvent.KEY_RELEASED,
                            1,
                            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                            java.awt.event.KeyEvent.VK_F5,
                            'A'
                        )
                    ),
                    mockk()
                )

                verify { onFullscreenKeyReleased.invoke() }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun onKeyEventEscape() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(
                        java.awt.event.KeyEvent(
                            mockk(),
                            java.awt.event.KeyEvent.KEY_RELEASED,
                            1,
                            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                            java.awt.event.KeyEvent.VK_ESCAPE,
                            'A'
                        )
                    ),
                    mockk()
                )

                verify { onEscapeKeyReleased.invoke() }
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
        val backgroundUrl = javaClass.classLoader.getResource("images/icon/ic_calendar.xml")?.toString() ?: return fail()
        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<URL>()) } returns BufferedImage(1, 1, Image.SCALE_FAST)

        subject.loadImage(backgroundUrl)

        verify { ImageIO.read(any<URL>()) }
        verify { anyConstructed<ImageCache>().put(any(), any()) }
    }

    @Test
    fun setSliderVisibility() {
        assertEquals(0f, subject.sliderAlpha())

        subject.setSliderVisibility(true)

        assertEquals(1f, subject.sliderAlpha())
    }
}