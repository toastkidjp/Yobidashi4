package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class WebTabViewKtTest {

    @MockK
    private lateinit var webViewPool: WebViewPool

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { webViewPool } bind(WebViewPool::class)
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }

        every { webViewPool.find(any(), any(), any()) } just Runs
        every { webViewPool.reload(any()) } just Runs
        every { webViewPool.clearFind(any()) } just Runs
        every { mainViewModel.showingSnackbar() } returns false
        every { mainViewModel.finderFlow() } returns emptyFlow()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webTabView() {
        runDesktopComposeUiTest {
            setContent {
                ComposePanel().setContent {
                    WebTabView(WebTab("test", "https://www.yahoo.com"))
                }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun isNotReadableUrl() {
        runDesktopComposeUiTest {
            setContent {
                ComposePanel().setContent {
                    val tab = mockk<WebTab>()
                    every { tab.isReadableUrl() } returns false
                    every { tab.id() } returns "test"
                    every { tab.url() } returns "test"

                    WebTabView(tab)

                    verify { tab.isReadableUrl() }
                    verify(inverse = true) { tab.id() }
                    verify(inverse = true) { tab.url() }
                }
            }
        }
    }

}