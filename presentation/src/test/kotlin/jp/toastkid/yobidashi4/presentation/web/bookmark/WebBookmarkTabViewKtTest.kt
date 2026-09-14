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
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.presentation.component.LoadIconViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class WebBookmarkTabViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: WebBookmarkTabViewModel

    @RelaxedMockK
    private lateinit var tab: WebBookmarkTab

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        
        mockkConstructor(WebIcon::class, LoadIconViewModel::class)
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
        /*every { tab.scrollPosition() } returns 0
        every { tab.withNewPosition(any()) } returns mockk()
        every { mainViewModel.updateScrollableTab(any(), any()) } just Runs*/

        coEvery { viewModel.launch(any()) } just Runs
        every { viewModel.openUrl(any(), any()) } just Runs
        every { viewModel.browseUri(any()) } just Runs
        every { viewModel.clipText(any()) } just Runs
        every { viewModel.delete(any()) } just Runs
        every { viewModel.focusRequester() } returns FocusRequester()
        every { viewModel.listState() } returns LazyListState(0)
        every { viewModel.bookmarks() } returns listOf(
            Bookmark("test item", "https://www.yahoo.co.jp"),
            Bookmark("icon item", "https://www.icon.co.jp")
        )
        every { viewModel.scrollEventFlow() } returns MutableSharedFlow()

        every { anyConstructed<LoadIconViewModel>().loadBitmap(any()) } returns ImageBitmap(1, 1)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webBookmarkTabView() {
        every { viewModel.openingDropdown(any()) } returns false
        val mutableSharedFlow = MutableSharedFlow<Float>(replay = 1, extraBufferCapacity = 1)
        every { viewModel.scrollEventFlow() } returns mutableSharedFlow

        runDesktopComposeUiTest {
            setContent {
                WebBookmarkTabView(tab, viewModel)
            }

            val item = onNodeWithText("test item")
            item.performClick()
            verify { viewModel.openUrl(any(), false) }
            item.performMouseInput {
                longClick()
                enter()
                exit()
            }
            item.performKeyInput {
                pressKey(Key.DirectionUp, 1000L)
            }

            verify { viewModel.scrollEventFlow() }
            verify { viewModel.listState() }
            mutableSharedFlow.tryEmit(-1f)
            mainClock.advanceTimeBy(500L)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webBookmarkTabViewWithDropdown() {
        every { viewModel.openingDropdown(any()) } answers {
            (this.args[0] as? Bookmark)?.title == "test item"
        }
        runDesktopComposeUiTest {
            setContent {
                WebBookmarkTabView(tab, viewModel)
            }

            verify { viewModel.bookmarks() }

            onNode(hasText("Open"), true).onParent().performClick()
            verify { viewModel.openUrl(any(), false) }

            onNode(hasText("Open background"), true).onParent().performClick()
            verify { viewModel.openUrl(any(), true) }

            onNode(hasText("Open with browser"), true).onParent().performClick()
            verify { viewModel.browseUri(any()) }

            onNode(hasText("Copy title"), true).onParent().performClick()
            verify { viewModel.clipText(any()) }

            onNode(hasText("Copy URL"), true).onParent().performClick()
            verify { viewModel.clipText(any()) }

            onNode(hasText("Clip markdown link"), true).onParent().performClick()
            verify { viewModel.clipText(any()) }

            onNode(hasText("Delete"), true).onParent().performClick()
            verify { viewModel.delete(any()) }
        }
    }

}