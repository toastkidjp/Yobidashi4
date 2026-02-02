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
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.domain.repository.input.InputHistoryRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class WebSearchBoxKtTest {

    @MockK
    private lateinit var inputHistoryRepository: InputHistoryRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { inputHistoryRepository } bind(InputHistoryRepository::class)
                }
            )
        }

        every { inputHistoryRepository.filter(any()) } returns emptyList()
        mockkConstructor(WebSearchBoxViewModel::class)
        every { anyConstructed<WebSearchBoxViewModel>().openingDropdown() } returns false
        every { anyConstructed<WebSearchBoxViewModel>().containsSwingContent() } returns false
        every { anyConstructed<WebSearchBoxViewModel>().existsResult() } returns true
        every { anyConstructed<WebSearchBoxViewModel>().showWebSearch() } returns true
        every { anyConstructed<WebSearchBoxViewModel>().setShowWebSearch(any()) } just Runs
        every { anyConstructed<WebSearchBoxViewModel>().setSaveSearchHistory(any()) } just Runs
        every { anyConstructed<WebSearchBoxViewModel>().switchSaveSearchHistory() } just Runs
        every { anyConstructed<WebSearchBoxViewModel>().setOpenDropdown() } just Runs
        every { anyConstructed<WebSearchBoxViewModel>().closeDropdown() } just Runs
        every { anyConstructed<WebSearchBoxViewModel>().choose(any<WebSearchItem>()) } just Runs
        every { anyConstructed<WebSearchBoxViewModel>().start() } just Runs
        val mutableStateListOf = mutableStateListOf<WebSearchItem>()
        SearchSite.entries.map(WebSearchItem::fromSearchSite).forEach(mutableStateListOf::add)
        every { anyConstructed<WebSearchBoxViewModel>().items() } returns mutableStateListOf
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webSearchBox() {
        val text = "web-search-input"
        every { anyConstructed<WebSearchBoxViewModel>().query() } returns TextFieldState(text)
        every { anyConstructed<WebSearchBoxViewModel>().invokeSearch() } just Runs

        runDesktopComposeUiTest {
            setContent {
                WebSearchBox()
            }

            verify { anyConstructed<WebSearchBoxViewModel>().showWebSearch() }

            onNodeWithContentDescription("Close web search box.", true).performClick()
            verify { anyConstructed<WebSearchBoxViewModel>().setShowWebSearch(false) }

            onNodeWithContentDescription("Switch dropdown menu.", true).performClick()
            verify { anyConstructed<WebSearchBoxViewModel>().setOpenDropdown() }

            onNodeWithContentDescription("save search history", true).performClick()
            verify { anyConstructed<WebSearchBoxViewModel>().setSaveSearchHistory(any()) }

            onNodeWithText("Save search history", true).performClick()
            verify { anyConstructed<WebSearchBoxViewModel>().switchSaveSearchHistory() }

            onNode(hasText(text), useUnmergedTree = true).performImeAction()
            verify { anyConstructed<WebSearchBoxViewModel>().invokeSearch() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withDropdown() {
        every { anyConstructed<WebSearchBoxViewModel>().openingDropdown() } returns true
        runDesktopComposeUiTest {
            setContent {
                WebSearchBox()
            }

            verify { anyConstructed<WebSearchBoxViewModel>().openingDropdown() }

            onNodeWithContentDescription(SearchSite.SEARCH_WITH_IMAGE.siteName, useUnmergedTree = true)
                .onParent()
                .performClick()
            verify { anyConstructed<WebSearchBoxViewModel>().choose(any<WebSearchItem>()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun containsSwingContent() {
        every { anyConstructed<WebSearchBoxViewModel>().openingDropdown() } returns true
        every { anyConstructed<WebSearchBoxViewModel>().containsSwingContent() } returns true

        runDesktopComposeUiTest {
            setContent {
                WebSearchBox()
            }

            verify { anyConstructed<WebSearchBoxViewModel>().openingDropdown() }
            verify { anyConstructed<WebSearchBoxViewModel>().showWebSearch() }

            onNodeWithContentDescription(SearchSite.SEARCH_WITH_IMAGE.siteName, useUnmergedTree = true).performClick()
            verify { anyConstructed<WebSearchBoxViewModel>().choose(any<WebSearchItem>()) }

            onNodeWithContentDescription("Switch dropdown menu.", true).performClick()
            verify { anyConstructed<WebSearchBoxViewModel>().closeDropdown() }
        }
    }
}