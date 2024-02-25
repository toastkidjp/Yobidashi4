package jp.toastkid.yobidashi4.presentation.web.bookmark

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class WebBookmarkTabViewKtTest {

    @MockK
    private lateinit var repository: BookmarkRepository

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var webBookmarkTab: WebBookmarkTab

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { repository } bind (BookmarkRepository::class)
                }
            )
        }
        every { repository.list() } returns listOf(Bookmark("test", "https://www.yahoo.co.jp"))
        mockkConstructor(WebIcon::class)
        every { anyConstructed<WebIcon>().readAll() } returns emptyList()
        every { anyConstructed<WebIcon>().makeFolderIfNeed() } just Runs
        every { anyConstructed<WebIcon>().find(any()) } returns null
        every { webBookmarkTab.scrollPosition() } returns 0
        every { webBookmarkTab.withNewPosition(any()) } returns mockk()
        every { mainViewModel.updateScrollableTab(any(), any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webBookmarkTabView() {
        runDesktopComposeUiTest {
            setContent {
                WebBookmarkTabView(webBookmarkTab)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webBookmarkTabViewWithDropdown() {
        mockkConstructor(WebBookmarkTabViewModel::class)
        every { anyConstructed<WebBookmarkTabViewModel>().openingDropdown(any()) } returns true

        runDesktopComposeUiTest {
            setContent {
                WebBookmarkTabView(webBookmarkTab)
            }
        }
    }

}