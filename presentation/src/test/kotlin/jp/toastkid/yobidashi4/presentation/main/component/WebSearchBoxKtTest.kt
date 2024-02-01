package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebSearchBoxKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(WebSearchBoxViewModel::class)
        every { anyConstructed<WebSearchBoxViewModel>().openingDropdown() } returns true
        every { anyConstructed<WebSearchBoxViewModel>().containsSwingContent() } returns false
        every { anyConstructed<WebSearchBoxViewModel>().existsResult() } returns true
        every { anyConstructed<WebSearchBoxViewModel>().showWebSearch() } returns true
        every { anyConstructed<WebSearchBoxViewModel>().start() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webSearchBox() {
        runDesktopComposeUiTest {
            setContent {
                WebSearchBox()
            }
        }
    }
}