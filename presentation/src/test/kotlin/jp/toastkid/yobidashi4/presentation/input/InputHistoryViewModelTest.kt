package jp.toastkid.yobidashi4.presentation.input

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.tab.InputHistoryTab
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import jp.toastkid.yobidashi4.presentation.lib.input.InputHistoryService
import jp.toastkid.yobidashi4.presentation.main.component.AggregationInvoker
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.stream.Stream

class InputHistoryViewModelTest {

    private lateinit var subject: InputHistoryViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var keywordSearch: FullTextArticleFinder

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { keywordSearch } bind (FullTextArticleFinder::class)
                    single(qualifier = null) { articlesReaderService } bind (ArticlesReaderService::class)
                }
            )
        }

        every { mainViewModel.initialAggregationType() } returns 0
        every { keywordSearch.invoke(any()) } returns mockk()
        every { articlesReaderService.invoke() } returns Stream.empty()
        mockkConstructor(InputHistoryService::class, AggregationInvoker::class)
        every { anyConstructed<InputHistoryService>().delete(any(), any()) } just Runs
        every { anyConstructed<InputHistoryService>().all(any()) } just Runs
        every { anyConstructed<AggregationInvoker>().invoke(any(), any()) } just Runs

        subject = InputHistoryViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun listState() {
        assertNotNull(subject.listState())
    }

    @Test
    fun items() {
        assertTrue(subject.items().isEmpty())
    }

    @Test
    fun onPointerEvent() {
        subject.onPointerEvent(mockk(), mockk())
    }

    @Test
    fun open() {
        subject.open(InputHistory("test", 1))

        verify { anyConstructed<AggregationInvoker>().invoke(any(), any()) }
    }

    @Test
    fun openOnBackground() {
        subject.open(mockk())

        verify { anyConstructed<AggregationInvoker>().invoke(any(), any()) }
    }

    @Test
    fun dateTimeString() {
        assertEquals("2025-04-23(Wed)09:55:47", subject.dateTimeString(InputHistory("test", 1745369747982)))
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun launch() {
        runComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                subject.launch(coroutineScope, InputHistoryTab("test"))
                subject.delete(InputHistory("test", 1))

                verify { anyConstructed<InputHistoryService>().all(any()) }
            }
        }
    }

    @Test
    fun onDispose() {
        every { mainViewModel.updateScrollableTab(any(), any()) } just Runs

        subject.onDispose(InputHistoryTab("test"))

        verify { mainViewModel.updateScrollableTab(any(), any()) }
    }

}