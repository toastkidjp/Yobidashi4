package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.mutableStateListOf
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
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.Tab
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
    private lateinit var  setting: Setting

    @MockK
    private lateinit var  articleFactory: ArticleFactory

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { viewModel } bind(MainViewModel::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                    single(qualifier=null) { articleFactory } bind(ArticleFactory::class)
                }
            )
        }

        every { viewModel.setShowInputBox(any()) } just Runs
        every { viewModel.addNewArticle(mockk()) } just Runs
        every { viewModel.edit(any()) } just Runs
        every { viewModel.openArticleList() } returns false
        every { viewModel.switchArticleList() } just Runs
        every { viewModel.showAggregationBox() } returns false
        every { viewModel.setInitialAggregationType(any()) } just Runs
        every { viewModel.switchAggregationBox(any()) } just Runs
        every { viewModel.openFile(any()) } just Runs
        every { viewModel.openTab(any()) } just Runs
        every { viewModel.openFileListTab(any(), any(), any(), any()) } just Runs
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
        every { viewModel.tabs } returns mutableStateListOf()
        every { viewModel.slideshow(any()) } just Runs
        every { viewModel.setSelectedIndex(any()) } just Runs
        every { viewModel.openMemoryUsageBox() } returns false
        every { viewModel.loadBackgroundImage() } just Runs
        every { viewModel.setShowWebSearch(any()) } just Runs
        every { viewModel.setInitialAggregationType(any()) } just Runs

        every { setting.articleFolderPath() } returns mockk()
        every { setting.userAgentName() } returns "test"
        every { setting.setUserAgentName(any()) } just Runs
        every { setting.save() } just Runs

        val article = mockk<Article>()
        every { articleFactory.withTitle(any()) } returns article
        every { article.makeFile(any()) } just Runs

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
}