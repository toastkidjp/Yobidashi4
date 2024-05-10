package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.StocksAggregationResult
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

class TableViewModelTest {

    private lateinit var subject: TableViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var articleFactory: ArticleFactory

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { articleFactory } bind (ArticleFactory::class)
                }
            )
        }
        every { mainViewModel.openPreview(any()) } just Runs
        every { mainViewModel.edit(any()) } just Runs
        val article = mockk<Article>()
        every { article.path() } returns mockk()
        every { articleFactory.withTitle(any()) } returns article
        mockkConstructor(KeywordHighlighter::class)
        every { anyConstructed<KeywordHighlighter>().invoke(any(), any()) } returns mockk()

        subject = TableViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun scrollAction() {
        subject.scrollAction(CoroutineScope(Dispatchers.Unconfined), Key.DirectionDown, true)

        assertNotNull(subject.listState())
    }

    @Test
    fun start() {
        assertTrue(subject.items().isEmpty())

        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { focusRequester.requestFocus() } just Runs
        every { subject.focusRequester() } returns focusRequester
        val result = StepsAggregationResult()
        result.put("2023-12-25", 1200, 95)
        result.put("2023-12-26", 2000, 122)

        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.start(TableTab("test", result))
        }

        verify { focusRequester.requestFocus() }
        assertEquals(2, subject.items().size)

        subject.sort(1, result)
    }

    @Test
    fun startWithExtractingQuery() {
        assertTrue(subject.items().isEmpty())

        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { focusRequester.requestFocus() } just Runs
        every { subject.focusRequester() } returns focusRequester
        val result = FindResult("test")
        result.add("2024-03-23.md", listOf("test"))

        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.start(TableTab("test", result))
        }

        verify { focusRequester.requestFocus() }
        assertEquals(1, subject.items().size)

        subject.sort(1, result)
    }

    @Test
    fun openMarkdownPreview() {
        subject.openMarkdownPreview("test")

        verify { articleFactory.withTitle(any()) }
        verify { mainViewModel.openPreview(any()) }
    }

    @Test
    fun edit() {
        subject.edit("test")

        verify { articleFactory.withTitle(any()) }
        verify { mainViewModel.edit(any()) }
    }

    @Test
    fun highlight() {
        subject.highlight("It longs to get them.")

        verify { anyConstructed<KeywordHighlighter>().invoke(any(), any()) }
    }

    @Test
    fun sort() {
        val result = StocksAggregationResult()
        result.put("2023-12-25", 1200, 200, 9.5)
        result.put("2023-12-26", 2000, 280, 12.2)

        subject.sort(2, result)
        subject.sort(2, result)
        subject.sort(2, result)
        subject.sort(3, result)
        subject.sort(3, result)
        subject.sort(3, result)
    }

    @Test
    fun makeWeight() {
        assertEquals(0.4f, subject.makeWeight(0))
        assertEquals(1f, subject.makeWeight(1))
    }

    @Test
    fun makeText() {
        assertEquals("test", subject.makeText("test"))
        assertEquals("12.34", subject.makeText(12.34))
        assertEquals("1,234", subject.makeText(1234))
    }

    @Test
    fun onDispose() {
        every { mainViewModel.updateScrollableTab(any(), any()) } just Runs

        subject.onDispose(mockk())

        verify { mainViewModel.updateScrollableTab(any(), any()) }
    }

}