package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import jp.toastkid.yobidashi4.presentation.slideshow.viewmodel.SlideshowViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideshowWindowTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(SlideshowViewModel::class)
        every { anyConstructed<SlideshowViewModel>().windowVisible() } returns false
        mockkStatic(Files::class)
        every { Files.lines(any()) } returns """
# Test
""".split("\n").stream()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun openWindow() {
        runDesktopComposeUiTest {
            setContent {
                SlideshowWindow().openWindow(mockk()) {}
            }
        }
    }

}