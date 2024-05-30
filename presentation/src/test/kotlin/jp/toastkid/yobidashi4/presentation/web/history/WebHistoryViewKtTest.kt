package jp.toastkid.yobidashi4.presentation.web.history

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.focus.FocusRequester
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
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class WebHistoryViewKtTest {

    @MockK
    private lateinit var repository: WebHistoryRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { mockk<MainViewModel>() } bind (MainViewModel::class)
                    single(qualifier = null) { repository } bind (WebHistoryRepository::class)
                }
            )
        }
        every { repository.readAll() } returns listOf(WebHistory("test", "https://www.yahoo.co.jp"))
        mockkConstructor(WebIcon::class)
        every { anyConstructed<WebIcon>().readAll() } returns emptyList()
        mockkConstructor(WebHistoryViewModel::class)
        every { anyConstructed<WebHistoryViewModel>().launch(any(), any()) } just Runs
        every { anyConstructed<WebHistoryViewModel>().onDispose(any()) } just Runs
        every { anyConstructed<WebHistoryViewModel>().listState() } returns LazyListState(0)
        every { anyConstructed<WebHistoryViewModel>().list() } returns mutableListOf(
            WebHistory("test item", "https://test.com/test"),
        )
        every { anyConstructed<WebHistoryViewModel>().openUrl(any(), any()) } just Runs
        every { anyConstructed<WebHistoryViewModel>().focusRequester() } returns FocusRequester()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webHistoryView() {
        runDesktopComposeUiTest {
            setContent {
                WebHistoryView(mockk())
            }

            val item = onNodeWithText("test item")
            item.performClick()
            verify { anyConstructed<WebHistoryViewModel>().openUrl(any(), false) }
            item.performMouseInput {
                longClick()
                enter()
                exit()
            }

            item.performKeyInput {
                pressKey(Key.DirectionUp, 1000L)
                pressKey(Key.DirectionDown, 1000L)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun useDropdown() {
        every { anyConstructed<WebHistoryViewModel>().openingDropdown(any()) } returns true
        every { anyConstructed<WebHistoryViewModel>().browseUri(any()) } just Runs
        every { anyConstructed<WebHistoryViewModel>().clipText(any()) } just Runs
        every { anyConstructed<WebHistoryViewModel>().delete(any()) } just Runs
        every { anyConstructed<WebHistoryViewModel>().clear() } just Runs
        every { anyConstructed<WebHistoryViewModel>().closeDropdown() } just Runs

        runDesktopComposeUiTest {
            setContent {
                WebHistoryView(mockk())
            }

            onNode(hasText("Open"), true).onParent().performClick()
            verify { anyConstructed<WebHistoryViewModel>().openUrl(any(), any()) }

            onNode(hasText("Open background"), true).onParent().performClick()
            verify { anyConstructed<WebHistoryViewModel>().openUrl(any(), any()) }

            onNode(hasText("Open with browser"), true).onParent().performClick()
            verify { anyConstructed<WebHistoryViewModel>().browseUri(any()) }

            onNode(hasText("Copy title"), true).onParent().performClick()
            verify { anyConstructed<WebHistoryViewModel>().clipText(any()) }

            onNode(hasText("Copy URL"), true).onParent().performClick()
            verify { anyConstructed<WebHistoryViewModel>().clipText(any()) }

            onNode(hasText("Clip markdown link"), true).onParent().performClick()
            verify { anyConstructed<WebHistoryViewModel>().clipText(any()) }

            onNode(hasText("Delete"), true).onParent().performClick()
            verify { anyConstructed<WebHistoryViewModel>().delete(any()) }

            onNode(hasText("Clear"), true).onParent().performClick()
            verify { anyConstructed<WebHistoryViewModel>().clear() }
        }
    }

}