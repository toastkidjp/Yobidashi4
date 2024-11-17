package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.window.Window
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.unmockkConstructor
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.service.article.finder.AsynchronousArticleIndexerService
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainMenuKtTest {

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var asynchronousArticleIndexerService: AsynchronousArticleIndexerService

    @MockK
    private lateinit var  setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { viewModel } bind(MainViewModel::class)
                    single(qualifier=null) { asynchronousArticleIndexerService } bind(AsynchronousArticleIndexerService::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        every { viewModel.setShowInputBox(any()) } just Runs
        every { viewModel.edit(any()) } just Runs
        every { viewModel.openArticleList() } returns false
        every { viewModel.switchArticleList() } just Runs
        every { viewModel.showAggregationBox() } returns false
        every { viewModel.setInitialAggregationType(any()) } just Runs
        every { viewModel.switchAggregationBox(any()) } just Runs
        every { viewModel.openFile(any()) } just Runs
        every { viewModel.openTab(any()) } just Runs
        every { viewModel.openFileListTab(any(), any(), any()) } just Runs
        every { viewModel.openUrl(any(), any()) } just Runs
        every { viewModel.openTextFile(any()) } just Runs
        val tab = mockk<Tab>()
        every { viewModel.currentTab() } returns tab
        every { tab.title() } returns "test"
        every { viewModel.saveCurrentEditorTab() } just Runs
        every { viewModel.switchFind() } just Runs
        every { viewModel.switchUseBackground() } just Runs
        every { viewModel.switchDarkMode() } just Runs
        every { viewModel.switchMemoryUsageBox() } just Runs
        every { viewModel.toggleFullscreen() } just Runs
        every { viewModel.toggleNarrowWindow() } just Runs
        every { viewModel.toggleFullscreenLabel() } returns "test"
        every { viewModel.closeCurrent() } just Runs
        every { viewModel.closeAllTabs() } just Runs
        every { viewModel.closeOtherTabs() } just Runs
        every { viewModel.closeSlideshow() } just Runs
        every { viewModel.showSnackbar(any(), any(), any()) } just Runs
        every { viewModel.tabs } returns mutableListOf()
        every { viewModel.slideshow(any()) } just Runs
        every { viewModel.setSelectedIndex(any()) } just Runs
        every { viewModel.openMemoryUsageBox() } returns false
        every { viewModel.loadBackgroundImage() } just Runs
        every { viewModel.setShowWebSearch(any()) } just Runs
        every { viewModel.setInitialAggregationType(any()) } just Runs
        every { viewModel.openWorldTime() } returns true

        every { setting.articleFolderPath() } returns mockk()
        every { setting.userAgentName() } returns "test"
        every { setting.setUserAgentName(any()) } just Runs
        every { setting.save() } just Runs

        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun test() {
        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu {  }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun currentIsEditorTab() {
        val editorTab = mockk<EditorTab>()
        every { viewModel.currentTab() } returns editorTab
        every { editorTab.showPreview() } returns true
        every { editorTab.switchPreview() } just Runs
        every { editorTab.path } returns mockk()

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu {  }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun currentIsWebTab() {
        val tab = mockk<WebTab>()
        every { viewModel.currentTab() } returns tab

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu {  }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun currentIsMarkdownPreviewTab() {
        val tab = mockk<MarkdownPreviewTab>()
        every { viewModel.currentTab() } returns tab
        every { tab.slideshowSourcePath() } returns mockk()

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu {  }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun useAdditionalTabMenu() {
        mockkConstructor(MainMenuViewModel::class)
        every { anyConstructed<MainMenuViewModel>().useAdditionalTabMenu() } returns true
        every { anyConstructed<MainMenuViewModel>().currentIsWebTab() } returns true

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu {  }
                }
            }
        }

        unmockkConstructor(MainMenuViewModel::class)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun canMoveTab() {
        mockkConstructor(MainMenuViewModel::class)
        every { anyConstructed<MainMenuViewModel>().canMoveTab() } returns true

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu {  }
                }
            }
        }

        unmockkConstructor(MainMenuViewModel::class)
    }

}