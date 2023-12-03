package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
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

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
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
        every { mainViewModel.slideshowPath() } returns mockk()
        every { mainViewModel.closeSlideshow() } just Runs
        every { mainViewModel.loadBackgroundImage() } just Runs
        every { mainViewModel.openArticleList() } returns false
        every { mainViewModel.articles() } returns emptyList()
        every { mainViewModel.reloadAllArticle() } just Runs
        every { mainViewModel.tabs } returns mutableStateListOf<Tab>()

    /*    mockkConstructor(TextFileReceiver::class, SlideshowWindow::class)
        every { anyConstructed<TextFileReceiver>().launch() } just Runs
        every { anyConstructed<SlideshowWindow>().openWindow(any(), any()) } just Runs*/
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