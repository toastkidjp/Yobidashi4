package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_web
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebSearchBoxKtTest {
    
    @RelaxedMockK
    private lateinit var viewModel: WebSearchBoxViewModel
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { viewModel.openingDropdown() } returns false
        every { viewModel.containsSwingContent() } returns false
        every { viewModel.existsResult() } returns true
        every { viewModel.showWebSearch() } returns true
        every { viewModel.setShowWebSearch(any()) } just Runs
        every { viewModel.setSaveSearchHistory(any()) } just Runs
        every { viewModel.switchSaveSearchHistory() } just Runs
        every { viewModel.setOpenDropdown() } just Runs
        every { viewModel.closeDropdown() } just Runs
        every { viewModel.choose(any<WebSearchItem>()) } just Runs
        every { viewModel.start() } just Runs
        val mutableStateListOf = mutableStateListOf<WebSearchItem>()
        SearchSite.entries.map(WebSearchItem::fromSearchSite).forEach(mutableStateListOf::add)
        every { viewModel.items() } returns mutableStateListOf
        every { viewModel.query() } returns TextFieldState()
        every { viewModel.currentIconPath() } returns Res.drawable.ic_web
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webSearchBox() {
        val text = "web-search-input"
        every { viewModel.query() } returns TextFieldState(text)
        every { viewModel.invokeSearch() } just Runs

        runDesktopComposeUiTest {
            setContent {
                WebSearchBox(viewModel)
            }

            verify { viewModel.showWebSearch() }

            onNodeWithContentDescription("Close web search box.", true).performClick()
            verify { viewModel.setShowWebSearch(false) }

            onNodeWithContentDescription("Switch dropdown menu.", true).performClick()
            verify { viewModel.setOpenDropdown() }

            onNodeWithContentDescription("save search history", true).performClick()
            verify { viewModel.setSaveSearchHistory(any()) }

            onNodeWithText("Save search history", true).performClick()
            verify { viewModel.switchSaveSearchHistory() }

            onNode(hasText(text), useUnmergedTree = true).performImeAction()
            verify { viewModel.invokeSearch() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withDropdown() {
        every { viewModel.openingDropdown() } returns true
        runDesktopComposeUiTest {
            setContent {
                WebSearchBox(viewModel)
            }

            verify { viewModel.openingDropdown() }

            onNodeWithContentDescription(SearchSite.SEARCH_WITH_IMAGE.siteName, useUnmergedTree = true)
                .onParent()
                .performClick()
            verify { viewModel.choose(any<WebSearchItem>()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun containsSwingContent() {
        every { viewModel.openingDropdown() } returns true
        every { viewModel.containsSwingContent() } returns true

        runDesktopComposeUiTest {
            setContent {
                WebSearchBox(viewModel)
            }

            verify { viewModel.openingDropdown() }
            verify { viewModel.showWebSearch() }

            onNodeWithContentDescription(SearchSite.SEARCH_WITH_IMAGE.siteName, useUnmergedTree = true).performClick()
            verify { viewModel.choose(any<WebSearchItem>()) }

            onNodeWithContentDescription("Switch dropdown menu.", true).performClick()
            verify { viewModel.closeDropdown() }
        }
    }
}