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
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.InputHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.NotificationListTab
import jp.toastkid.yobidashi4.domain.model.tab.SettingEditorTab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.main.content.tab.TabContentRegistry
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

class TabsViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: TabsViewModel
    
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

        every { viewModel.tabIsEmpty() } returns false
        every { viewModel.openingDropdown(any()) } returns false
        val mockk = mockk<TextFileViewerTab>()
        every { mockk.path() } returns mockk()
        every { viewModel.currentTab() } returns mockk
        every { viewModel.selectedTabIndex() } returns 1
        every { viewModel.isSelectedIndex(any()) } returns true
        every { viewModel.currentTabIndex(any()) } returns 0
        every { webTab.title() } returns "test"
        every { webTab.url() } returns "test"
        every { webTab.closeable() } returns true
        every { webTab.update() } returns emptyFlow()
        every { markdownPreviewTab.title() } returns "test"
        every { markdownPreviewTab.closeable() } returns false
        every { markdownPreviewTab.update() } returns emptyFlow()
        every { markdownPreviewTab.slideshowSourcePath() } returns mockk()
        every { tableTab.title() } returns "test"
        every { tableTab.closeable() } returns true
        every { tableTab.update() } returns emptyFlow()
        every { tableTab.reload() } just Runs
        every { tableTab.items() } returns StepsAggregationResult()
        every { editorTab.title() } returns "test"
        val editorTabsPath = mockk<Path>()
        every { editorTabsPath.nameWithoutExtension } returns "test"
        every { editorTab.filePath() } returns editorTabsPath
        every { editorTab.closeable() } returns true
        every { editorTab.update() } returns flowOf(1, 2, 3)
        every { viewModel.setSelectedIndex(any()) } just Runs
        every { viewModel.edit(any()) } just Runs
        every { viewModel.openFile(any()) } just Runs
        every { viewModel.removeTabAt(any()) } just Runs
        every { viewModel.onPointerEvent(any(), any()) } just Runs
        every { viewModel.closeOtherTabs() } just Runs
        every { viewModel.exportTable(any()) } just Runs
        every { viewModel.exportChat(any()) } just Runs
        every { viewModel.tabs() } returns listOf(
            webTab,
            markdownPreviewTab,
            tableTab,
            editorTab,
            BarcodeToolTab(),
            NotificationListTab(),
            ChatTab()
        )
        every { viewModel.clipText(any<String>()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tabsView() {
        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier, viewModel, TabContentRegistry.Builder().build())
            }

            onNode(hasText("Barcode tool")).assertExists("Not found!")
                .performMouseInput {
                    click()
                    rightClick()
                }

            verify { viewModel.setSelectedIndex(any()) }
            verify { viewModel.onPointerEvent(any(), any()) }

            onNodeWithContentDescription("Close button 0", useUnmergedTree = true)
                .performClick()
            verify { viewModel.removeTabAt(0) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dropdownWebTab() {
        every { viewModel.openingDropdown(any()) } returns true

        runDesktopComposeUiTest {
            every { viewModel.tabs() } returns listOf(webTab)

            setContent {
                TabsView(Modifier, viewModel, TabContentRegistry.Builder().build())
            }

            onNode(hasText("Copy title"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.clipText(any<String>()) }

            onNode(hasText("Close other tabs"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.closeOtherTabs() }

            onNode(hasText("Copy URL"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.clipText(any<String>()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dropdownMarkdownPreviewTab() {
        every { viewModel.openingDropdown(any()) } returns true
        every { viewModel.tabs() } returns listOf(markdownPreviewTab)

        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier, viewModel, TabContentRegistry.Builder().build())
            }

            onNode(hasText("Edit"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.edit(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dropdownTableTab() {
        every { viewModel.openingDropdown(any()) } returns true
        every { viewModel.tabs() } returns listOf(tableTab)

        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier, viewModel, TabContentRegistry.Builder().build())
            }

            onNode(hasText("Reload"), useUnmergedTree = true).onParent().performClick()
            verify { tableTab.reload() }
            onNode(hasText("Export table"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.exportTable(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dropdownEditorTab() {
        every { viewModel.openingDropdown(any()) } returns true
        every { viewModel.tabs() } returns listOf(editorTab)
        every { viewModel.slideshow(any()) } just Runs

        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier, viewModel, TabContentRegistry.Builder().build())
            }
            onNode(hasText("Clip internal link"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.clipText(any<String>()) }
            onNode(hasText("Open with editor"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.openFile(any()) }
            onNode(hasText("Slideshow"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.slideshow(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dropdownChatTab() {
        every { viewModel.openingDropdown(any()) } returns true
        every { viewModel.tabs() } returns listOf(ChatTab())

        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier, viewModel, TabContentRegistry.Builder().build())
            }
            onNode(hasText("Export chat"), useUnmergedTree = true).onParent().performClick()

            verify { viewModel.exportChat(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dropdownSettingEditorTab() {
        every { viewModel.openingDropdown(any()) } returns true
        every { viewModel.tabs() } returns listOf(SettingEditorTab())

        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier, viewModel, TabContentRegistry.Builder().build())
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun inputHistoryTab() {
        every { viewModel.openingDropdown(any()) } returns true
        every { viewModel.tabs() } returns listOf(InputHistoryTab("test"))

        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier, viewModel, TabContentRegistry.Builder().build())
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webBookmarkTab() {
        every { viewModel.openingDropdown(any()) } returns true
        every { viewModel.tabs() } returns listOf(WebBookmarkTab())

        runDesktopComposeUiTest {
            setContent {
                TabsView(Modifier, viewModel, TabContentRegistry.Builder().build())
            }

            onNode(hasText("Modify"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.openFile(any()) }
        }
    }

}