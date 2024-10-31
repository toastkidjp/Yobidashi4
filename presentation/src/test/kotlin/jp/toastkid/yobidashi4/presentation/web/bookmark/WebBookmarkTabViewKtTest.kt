package jp.toastkid.yobidashi4.presentation.web.bookmark

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.component.LoadIconViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.absolutePathString
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
        mockkConstructor(WebIcon::class, WebBookmarkTabViewModel::class, LoadIconViewModel::class)
        every { anyConstructed<WebIcon>().readAll() } returns emptyList()
        every { anyConstructed<WebIcon>().makeFolderIfNeed() } just Runs
        every { anyConstructed<WebIcon>().find(any()) } answers {
            val url = this.args.get(0) as? String ?: return@answers null
            if (url == "https://www.icon.co.jp") {
                val path = mockk<Path>()
                every { path.absolutePathString() } returns "icon/icon.png"
                return@answers path
            }
            return@answers null
        }
        every { webBookmarkTab.scrollPosition() } returns 0
        every { webBookmarkTab.withNewPosition(any()) } returns mockk()
        every { mainViewModel.updateScrollableTab(any(), any()) } just Runs

        every { anyConstructed<WebBookmarkTabViewModel>().launch(any(), any()) } just Runs
        every { anyConstructed<WebBookmarkTabViewModel>().openUrl(any(), any()) } just Runs
        every { anyConstructed<WebBookmarkTabViewModel>().browseUri(any()) } just Runs
        every { anyConstructed<WebBookmarkTabViewModel>().clipText(any()) } just Runs
        every { anyConstructed<WebBookmarkTabViewModel>().delete(any()) } just Runs
        every { anyConstructed<WebBookmarkTabViewModel>().focusRequester() } returns FocusRequester()
        every { anyConstructed<WebBookmarkTabViewModel>().listState() } returns LazyListState(0)
        every { anyConstructed<WebBookmarkTabViewModel>().bookmarks() } returns listOf(
            Bookmark("test item", "https://www.yahoo.co.jp"),
            Bookmark("icon item", "https://www.icon.co.jp")
        )
        every { anyConstructed<LoadIconViewModel>().loadBitmap(any()) } returns ImageBitmap(1, 1)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webBookmarkTabView() {
        every { anyConstructed<WebBookmarkTabViewModel>().openingDropdown(any()) } returns false

        runDesktopComposeUiTest {
            setContent {
                WebBookmarkTabView(webBookmarkTab)
            }

            val item = onNodeWithText("test item")
            item.performClick()
            verify { anyConstructed<WebBookmarkTabViewModel>().openUrl(any(), false) }
            item.performMouseInput {
                longClick()
                enter()
                exit()
            }
            item.performKeyInput {
                pressKey(Key.DirectionUp, 1000L)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webBookmarkTabViewWithDropdown() {
        every { anyConstructed<WebBookmarkTabViewModel>().openingDropdown(any()) } answers {
            (this.args[0] as? Bookmark)?.title == "test item"
        }
        runDesktopComposeUiTest {
            setContent {
                WebBookmarkTabView(webBookmarkTab)
            }

            verify { anyConstructed<WebBookmarkTabViewModel>().bookmarks() }

            onNode(hasText("Open"), true).onParent().performClick()
            verify { anyConstructed<WebBookmarkTabViewModel>().openUrl(any(), false) }

            onNode(hasText("Open background"), true).onParent().performClick()
            verify { anyConstructed<WebBookmarkTabViewModel>().openUrl(any(), true) }

            onNode(hasText("Open with browser"), true).onParent().performClick()
            verify { anyConstructed<WebBookmarkTabViewModel>().browseUri(any()) }

            onNode(hasText("Copy title"), true).onParent().performClick()
            verify { anyConstructed<WebBookmarkTabViewModel>().clipText(any()) }

            onNode(hasText("Copy URL"), true).onParent().performClick()
            verify { anyConstructed<WebBookmarkTabViewModel>().clipText(any()) }

            onNode(hasText("Clip markdown link"), true).onParent().performClick()
            verify { anyConstructed<WebBookmarkTabViewModel>().clipText(any()) }

            onNode(hasText("Delete"), true).onParent().performClick()
            verify { anyConstructed<WebBookmarkTabViewModel>().delete(any()) }
        }
    }

}