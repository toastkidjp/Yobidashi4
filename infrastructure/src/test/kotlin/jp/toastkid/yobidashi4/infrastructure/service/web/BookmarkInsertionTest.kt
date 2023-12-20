package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.cef.callback.CefContextMenuParams
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class BookmarkInsertionTest {

    @InjectMockKs
    private lateinit var bookmarkInsertion: BookmarkInsertion

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var repository: BookmarkRepository

    @MockK
    private lateinit var tab: Tab

    @MockK
    private lateinit var params: CefContextMenuParams

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single() { mainViewModel }.bind(MainViewModel::class)
                    single() { repository }.bind(BookmarkRepository::class)
                }
            )
        }

        every { mainViewModel.currentTab() } returns tab
        every { tab.title() } returns "tab"
        every { repository.add(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()

        unmockkAll()
    }

    @Test
    fun invoke() {
        val slot = slot<String>()
        every { mainViewModel.showSnackbar(capture(slot)) } just Runs

        bookmarkInsertion.invoke(null, null)
        assertEquals("Add bookmark: Bookmark(title=tab, url=, favicon=, parent=root, folder=false)", slot.captured)
    }

    @Test
    fun invoke2() {
        val slot = slot<String>()
        every { mainViewModel.showSnackbar(capture(slot)) } just Runs
        every { params.linkUrl } returns "https://www.yahoo.co.jp"

        bookmarkInsertion.invoke(params, null)
        assertEquals("Add bookmark: Bookmark(title=https://www.yahoo.co.jp, url=https://www.yahoo.co.jp, favicon=, parent=root, folder=false)", slot.captured)
    }

    @Test
    fun invoke3() {
        val slot = slot<String>()
        every { mainViewModel.showSnackbar(capture(slot)) } just Runs
        every { params.linkUrl } returns null
        every { params.sourceUrl } returns "https://www.yahoo.co.jp"

        bookmarkInsertion.invoke(params, "")

        assertEquals("Add bookmark: Bookmark(title=https://www.yahoo.co.jp, url=https://www.yahoo.co.jp, favicon=, parent=root, folder=false)", slot.captured)
    }

    @Test
    fun invokeWithLinkUrlIsEmpty() {
        val slot = slot<String>()
        every { mainViewModel.showSnackbar(capture(slot)) } just Runs
        every { params.linkUrl } returns ""
        every { params.sourceUrl } returns "https://www.yahoo.co.jp"

        bookmarkInsertion.invoke(params, "")

        assertEquals("Add bookmark: Bookmark(title=https://www.yahoo.co.jp, url=https://www.yahoo.co.jp, favicon=, parent=root, folder=false)", slot.captured)
    }

    @Test
    fun invoke4() {
        val slot = slot<String>()
        every { mainViewModel.showSnackbar(capture(slot)) } just Runs
        every { params.linkUrl } returns null
        every { params.sourceUrl } returns null
        every { params.pageUrl } returns "https://www.yahoo.co.jp"

        bookmarkInsertion.invoke(params, "")

        assertEquals("Add bookmark: Bookmark(title=tab, url=https://www.yahoo.co.jp, favicon=, parent=root, folder=false)", slot.captured)
    }

    @Test
    fun elseCase() {
        val slot = slot<String>()
        every { mainViewModel.showSnackbar(capture(slot)) } just Runs
        every { params.linkUrl } returns null
        every { params.sourceUrl } returns null
        every { params.pageUrl } returns null

        bookmarkInsertion.invoke(params, "")

        assertEquals("Add bookmark: Bookmark(title=tab, url=, favicon=, parent=root, folder=false)", slot.captured)
    }

    @Test
    fun paramsIsNullCase() {
        val slot = slot<String>()
        every { mainViewModel.showSnackbar(capture(slot)) } just Runs
        every { params.linkUrl } returns null
        every { params.sourceUrl } returns null
        every { params.pageUrl } returns "https://www.yahoo.co.jp"

        bookmarkInsertion.invoke(null, "")

        assertEquals("Add bookmark: Bookmark(title=tab, url=, favicon=, parent=root, folder=false)", slot.captured)
    }

    @Test
    fun currentTabIsNull() {
        every { mainViewModel.currentTab() } returns tab
        val slot = slot<String>()
        every { mainViewModel.showSnackbar(capture(slot)) } just Runs
        every { params.linkUrl } returns "https://www.yahoo.co.jp"

        bookmarkInsertion.invoke(params, null)

        verify { mainViewModel.currentTab() }
        verify { mainViewModel.showSnackbar(capture(slot)) }
        verify { params.linkUrl }
        assertEquals("Add bookmark: Bookmark(title=https://www.yahoo.co.jp, url=https://www.yahoo.co.jp, favicon=, parent=root, folder=false)", slot.captured)
    }

}