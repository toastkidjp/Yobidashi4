package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.log.viewer.TextFileViewerTabViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TabsViewKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }

        mockkConstructor(TabsViewModel::class, TextFileViewerTabViewModel::class)
        every { anyConstructed<TabsViewModel>().tabIsEmpty() } returns false
        every { anyConstructed<TabsViewModel>().openingDropdown(any()) } returns true
        val mockk = mockk<TextFileViewerTab>()
        every { mockk.path() } returns mockk()
        coEvery { anyConstructed<TextFileViewerTabViewModel>().launch(any()) } just Runs
        every { anyConstructed<TabsViewModel>().currentTab() } returns mockk
        every { anyConstructed<TabsViewModel>().selectedTabIndex() } returns 1
        every { anyConstructed<TabsViewModel>().isSelectedIndex(any()) } returns true
        every { anyConstructed<TabsViewModel>().currentTabIndex(any()) } returns 0
        val webTab = mockk<WebTab>()
        every { webTab.title() } returns "test"
        every { webTab.iconPath() } returns "images/icon/ic_web.xml"
        every { webTab.closeable() } returns true
        every { webTab.update() } returns emptyFlow()
        val markdownPreviewTab = mockk<MarkdownPreviewTab>()
        every { markdownPreviewTab.title() } returns "test"
        every { markdownPreviewTab.iconPath() } returns "images/icon/ic_web.xml"
        every { markdownPreviewTab.closeable() } returns false
        every { markdownPreviewTab.update() } returns emptyFlow()
        val tableTab = mockk<TableTab>()
        every { tableTab.title() } returns "test"
        every { tableTab.iconPath() } returns "images/icon/ic_web.xml"
        every { tableTab.closeable() } returns true
        every { tableTab.update() } returns emptyFlow()
        val editorTab = mockk<EditorTab>()
        every { editorTab.title() } returns "test"
        every { editorTab.iconPath() } returns "images/icon/ic_web.xml"
        every { editorTab.closeable() } returns true
        every { editorTab.update() } returns emptyFlow()
        every { anyConstructed<TabsViewModel>().tabs() } returns listOf(
            webTab,
            markdownPreviewTab,
            tableTab,
            editorTab,
        )
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tabsView() {
        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dropdownClosedCase() {
        every { anyConstructed<TabsViewModel>().openingDropdown(any()) } returns true

        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier)
            }
        }
    }

}