package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.rightClick
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
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.NotificationListTab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.log.viewer.TextFileViewerTabViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.nameWithoutExtension
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
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

    @MockK
    private lateinit var webTab: WebTab

    @MockK
    private lateinit var editorTab: EditorTab

    @MockK
    private lateinit var markdownPreviewTab: MarkdownPreviewTab

    @MockK
    private lateinit var tableTab: TableTab

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

        mockkConstructor(TabsViewModel::class, TextFileViewerTabViewModel::class, ClipboardPutterService::class)
        every { anyConstructed<TabsViewModel>().tabIsEmpty() } returns false
        every { anyConstructed<TabsViewModel>().openingDropdown(any()) } returns false
        val mockk = mockk<TextFileViewerTab>()
        every { mockk.path() } returns mockk()
        coEvery { anyConstructed<TextFileViewerTabViewModel>().launch(any()) } just Runs
        every { anyConstructed<TabsViewModel>().currentTab() } returns mockk
        every { anyConstructed<TabsViewModel>().selectedTabIndex() } returns 1
        every { anyConstructed<TabsViewModel>().isSelectedIndex(any()) } returns true
        every { anyConstructed<TabsViewModel>().currentTabIndex(any()) } returns 0
        every { webTab.title() } returns "test"
        every { webTab.url() } returns "test"
        every { webTab.iconPath() } returns "images/icon/ic_web.xml"
        every { webTab.closeable() } returns true
        every { webTab.update() } returns emptyFlow()
        every { markdownPreviewTab.title() } returns "test"
        every { markdownPreviewTab.iconPath() } returns "images/icon/ic_web.xml"
        every { markdownPreviewTab.closeable() } returns false
        every { markdownPreviewTab.update() } returns emptyFlow()
        every { markdownPreviewTab.slideshowSourcePath() } returns mockk()
        every { tableTab.title() } returns "test"
        every { tableTab.iconPath() } returns "images/icon/ic_web.xml"
        every { tableTab.closeable() } returns true
        every { tableTab.update() } returns emptyFlow()
        every { tableTab.reload() } just Runs
        every { tableTab.items() } returns StepsAggregationResult()
        every { editorTab.title() } returns "test"
        every { editorTab.iconPath() } returns "images/icon/ic_web.xml"
        val editorTabsPath = mockk<Path>()
        every { editorTabsPath.nameWithoutExtension } returns "test"
        every { editorTab.path } returns editorTabsPath
        every { editorTab.closeable() } returns true
        every { editorTab.update() } returns flowOf(1, 2, 3)
        every { anyConstructed<TabsViewModel>().setSelectedIndex(any()) } just Runs
        every { anyConstructed<TabsViewModel>().edit(any()) } just Runs
        every { anyConstructed<TabsViewModel>().removeTabAt(any()) } just Runs
        every { anyConstructed<TabsViewModel>().onPointerEvent(any(), any()) } just Runs
        every { anyConstructed<TabsViewModel>().closeOtherTabs() } just Runs
        every { anyConstructed<TabsViewModel>().exportTable(any()) } just Runs
        every { anyConstructed<TabsViewModel>().exportChat(any()) } just Runs
        every { anyConstructed<TabsViewModel>().tabs() } returns listOf(
            webTab,
            markdownPreviewTab,
            tableTab,
            editorTab,
            BarcodeToolTab(),
            NotificationListTab(),
            ChatTab()
        )
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
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

            onNode(hasText("Barcode tool")).assertExists("Not found!")
                .performMouseInput {
                    click()
                    rightClick()
                }

            verify { anyConstructed<TabsViewModel>().setSelectedIndex(any()) }
            verify { anyConstructed<TabsViewModel>().onPointerEvent(any(), any()) }

            onNodeWithContentDescription("Close button 0", useUnmergedTree = true)
                .performClick()
            verify { anyConstructed<TabsViewModel>().removeTabAt(0) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dropdown() {
        every { anyConstructed<TabsViewModel>().openingDropdown(any()) } returns true

        runDesktopComposeUiTest {
            every { anyConstructed<TabsViewModel>().tabs() } returns listOf(webTab)

            setContent {
                TabsView(Modifier)
            }

            onNode(hasText("Copy title"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }

            onNode(hasText("Close other tabs"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<TabsViewModel>().closeOtherTabs() }

            onNode(hasText("Copy URL"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }

            every { anyConstructed<TabsViewModel>().tabs() } returns listOf(markdownPreviewTab)
            setContent {
                TabsView(Modifier)
            }
            onNode(hasText("Edit"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<TabsViewModel>().edit(any()) }

            every { anyConstructed<TabsViewModel>().tabs() } returns listOf(tableTab)
            setContent {
                TabsView(Modifier)
            }
            onNode(hasText("Reload"), useUnmergedTree = true).onParent().performClick()
            verify { tableTab.reload() }
            onNode(hasText("Export table"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<TabsViewModel>().exportTable(any()) }

            every { anyConstructed<TabsViewModel>().tabs() } returns listOf(editorTab)
            setContent {
                TabsView(Modifier)
            }
            onNode(hasText("Clip internal link"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }

            every { anyConstructed<TabsViewModel>().tabs() } returns listOf(ChatTab())
            setContent {
                TabsView(Modifier)
            }
            onNode(hasText("Export chat"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<TabsViewModel>().exportChat(any()) }
        }
    }

}