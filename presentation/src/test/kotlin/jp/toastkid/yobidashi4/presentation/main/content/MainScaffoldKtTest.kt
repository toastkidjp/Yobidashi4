package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.ConverterToolTab
import jp.toastkid.yobidashi4.domain.repository.input.InputHistoryRepository
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_search
import jp.toastkid.yobidashi4.presentation.main.component.AggregationBoxViewModel
import jp.toastkid.yobidashi4.presentation.main.component.WebSearchBoxViewModel
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItemMeta
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItemMetaExtractor
import jp.toastkid.yobidashi4.presentation.markdown.MarkdownTabViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.attribute.FileTime

class MainScaffoldKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @MockK
    private lateinit var fullTextArticleFinder: FullTextArticleFinder

    @MockK
    private lateinit var inputHistoryRepository: InputHistoryRepository

    @MockK
    private lateinit var metaExtractor: FileListItemMetaExtractor

    @RelaxedMockK
    private lateinit var tabsViewModel: TabsViewModel

    @RelaxedMockK
    private lateinit var webSearchBoxViewModel: WebSearchBoxViewModel

    @MockK
    private lateinit var aggregationBoxViewModel: AggregationBoxViewModel

    @RelaxedMockK
    private lateinit var markdownTabViewModel: MarkdownTabViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                    single(qualifier=null) { articlesReaderService } bind(ArticlesReaderService::class)
                    single(qualifier=null) { fullTextArticleFinder } bind(FullTextArticleFinder::class)
                    single(qualifier=null) { inputHistoryRepository } bind(InputHistoryRepository::class)
                    single(qualifier=null) { metaExtractor } bind(FileListItemMetaExtractor::class)
                    single(qualifier=null) { tabsViewModel } bind(TabsViewModel::class)
                    single(qualifier=null) { webSearchBoxViewModel } bind(WebSearchBoxViewModel::class)
                    single(qualifier=null) { aggregationBoxViewModel } bind(AggregationBoxViewModel::class)
                    single(qualifier=null) { markdownTabViewModel } bind(MarkdownTabViewModel::class)
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
        every { setting.chatApiKey() } returns "test-key"
        every { inputHistoryRepository.filter(any()) } returns emptyList()
        every { metaExtractor.make(any()) } returns FileListItemMeta(
            "test",
            20000
        )
        every { webSearchBoxViewModel.query() } returns TextFieldState()
        every { webSearchBoxViewModel.currentIconPath() } returns Res.drawable.ic_search
        every { markdownTabViewModel.scrollState() } returns LazyListState()

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

}