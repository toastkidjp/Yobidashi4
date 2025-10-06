package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files

class SlideshowWindowTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(SlideshowWindowViewModel::class)
        every { anyConstructed<SlideshowWindowViewModel>().windowVisible() } returns false
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

            onNodeWithContentDescription("slider").performKeyInput {
                pressKey(Key.Escape)
            }
        }
    }

}