package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.slideshow.Slide
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.domain.service.slideshow.SlideDeckReader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class SlideshowWindowTest {

    @MockK
    private lateinit var slideDeckReader: SlideDeckReader

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { slideDeckReader } bind(SlideDeckReader::class)
                }
            )
        }

        mockkConstructor(SlideshowWindowViewModel::class)
        every { anyConstructed<SlideshowWindowViewModel>().windowVisible() } returns false
        every { slideDeckReader.invoke(any()) } returns SlideDeck(slides = mutableListOf(Slide()))
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun openWindow() {
        runDesktopComposeUiTest {
            setContent {
                SlideshowWindow().openWindow(mockk()) {}
            }

            onNodeWithContentDescription("slider").performKeyInput {
                pressKey(Key.Escape)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun emptyDeckCase() {
        every { slideDeckReader.invoke(any()) } returns SlideDeck()

        runDesktopComposeUiTest {
            setContent {
                SlideshowWindow().openWindow(mockk()) {}
            }

            onNodeWithContentDescription("slider").assertDoesNotExist()
        }
    }

}