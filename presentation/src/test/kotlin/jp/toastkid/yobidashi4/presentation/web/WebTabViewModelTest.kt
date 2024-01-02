package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.ui.focus.FocusRequester
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Color
import java.awt.Panel
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.editor.finder.FindOrder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class WebTabViewModelTest {

    private lateinit var subject: WebTabViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var webViewPool: WebViewPool

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier = null) { webViewPool } bind(WebViewPool::class)
                }
            )
        }

        subject = WebTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun component() {
        val tab = mockk<WebTab>()
        every { tab.id() } returns "id-1"
        every { tab.url() } returns "https://www.yahoo.co.jp"
        every { webViewPool.component(any(), any()) } returns Panel()

        subject.component(tab, Color.BLACK)

        verify { webViewPool.component(any(), any()) }
    }

    @Test
    fun spacerHeight() {
        every { mainViewModel.showingSnackbar() } returns true

        val trueCase = subject.spacerHeight()

        every { mainViewModel.showingSnackbar() } returns false

        val falseCase = subject.spacerHeight()

        assertNotEquals(trueCase, falseCase)
    }

    @Test
    fun start() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs
        every { mainViewModel.finderFlow() } returns flowOf(FindOrder.EMPTY, FindOrder("test", ""))
        every { webViewPool.clearFind(any()) } just Runs
        every { webViewPool.find(any(), any(), any()) } just Runs

        runBlocking {
            subject.start("test")

            verify { focusRequester.requestFocus() }
        }
    }
}