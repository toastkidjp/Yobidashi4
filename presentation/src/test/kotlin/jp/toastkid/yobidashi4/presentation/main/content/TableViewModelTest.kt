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
import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.StocksAggregationResult
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        subject.start(result)

        verify { focusRequester.requestFocus() }
        assertEquals(2, subject.items().size)

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

}