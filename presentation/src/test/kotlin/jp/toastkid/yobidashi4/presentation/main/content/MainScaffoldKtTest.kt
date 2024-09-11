package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.ConverterToolTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainScaffoldKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @MockK
    private lateinit var fullTextArticleFinder: FullTextArticleFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { articlesReaderService } bind(ArticlesReaderService::class)
                    single(qualifier=null) { fullTextArticleFinder } bind(FullTextArticleFinder::class)
                }
            )
        }

        every { mainViewModel.snackbarHostState() } returns SnackbarHostState()
        every { mainViewModel.showBackgroundImage() } returns false
        every { mainViewModel.showWebSearch() } returns false
        every { mainViewModel.showAggregationBox() } returns false
        every { mainViewModel.openFind() } returns false
        every { mainViewModel.showInputBox() } returns false
        every { mainViewModel.openMemoryUsageBox() } returns false
        every { mainViewModel.openWorldTime() } returns false
        every { mainViewModel.slideshowPath() } returns mockk()
        every { mainViewModel.closeSlideshow() } just Runs
        every { mainViewModel.loadBackgroundImage() } just Runs
        every { mainViewModel.openArticleList() } returns false
        every { mainViewModel.articles() } returns emptyList()
        every { mainViewModel.reloadAllArticle() } just Runs
        every { mainViewModel.selected } returns mutableStateOf(0)
        every { mainViewModel.currentTab() } returns ConverterToolTab()
        every { mainViewModel.tabs } returns mutableListOf()

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.size(any()) } returns 20000
        every { Files.getLastModifiedTime(any()) } returns FileTime.fromMillis(System.currentTimeMillis())
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mainScaffold() {
        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun useOptionalComponents() {
        every { mainViewModel.backgroundImage() } returns ImageBitmap(1, 1)
        every { mainViewModel.showBackgroundImage() } returns true
        every { mainViewModel.showWebSearch() } returns true
        every { mainViewModel.showAggregationBox() } returns true
        every { mainViewModel.openFind() } returns true
        every { mainViewModel.showInputBox() } returns true
        every { mainViewModel.openMemoryUsageBox() } returns true
        val markdownPreviewTab = mockk<MarkdownPreviewTab>()
        val markdown = mockk<Markdown>()
        every { markdown.lines() } returns emptyList()
        every { markdownPreviewTab.markdown() } returns markdown
        every { markdownPreviewTab.scrollPosition() } returns 1
        every { mainViewModel.currentTab() } returns markdownPreviewTab
        every { mainViewModel.initialAggregationType() } returns 0
        every { mainViewModel.inputValue() } returns TextFieldValue("search")
        every { mainViewModel.findStatus() } returns "test"
        every { mainViewModel.caseSensitive() } returns true
        every { mainViewModel.updateScrollableTab(any(), any()) } just Runs

        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun useTabContents() {
        every { mainViewModel.selected } returns mutableStateOf(0)
        every { mainViewModel.currentTab() } returns LoanCalculatorTab()
        every { mainViewModel.tabs } returns mutableListOf(
            LoanCalculatorTab(),
            BarcodeToolTab(),
            FileRenameToolTab()
        )

        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun useFileRenameToolTabContents() {
        every { mainViewModel.selected } returns mutableStateOf(2)
        every { mainViewModel.currentTab() } returns FileRenameToolTab()
        every { mainViewModel.registerDroppedPathReceiver(any()) } just Runs
        every { mainViewModel.unregisterDroppedPathReceiver() } just Runs
        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs
        every { mainViewModel.tabs } returns mutableListOf(
            LoanCalculatorTab(),
            BarcodeToolTab(),
            FileRenameToolTab()
        )

        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun articles() {
        val path = mockk<Path>()
        every { path.extension } returns "md"
        every { mainViewModel.articles() } returns listOf(path)

        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun articlesWithOpenArticleList() {
        every { mainViewModel.openArticleList() } returns true
        every { mainViewModel.hideArticleList() } just Runs

        val path = mockk<Path>()
        every { path.extension } returns "md"
        every { mainViewModel.articles() } returns listOf(path)

        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }

            val listSwitch = onNodeWithContentDescription("Close file list.", useUnmergedTree = true)
            listSwitch
                .performMouseInput {
                    enter()
                    exit()
                    enter()
                    click()
                    exit()
                }

            verify { mainViewModel.hideArticleList() }
        }
    }

}