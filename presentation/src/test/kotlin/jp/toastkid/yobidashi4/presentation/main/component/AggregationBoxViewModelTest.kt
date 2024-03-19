package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.service.archive.KeywordArticleFinder
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import jp.toastkid.yobidashi4.presentation.lib.input.InputHistoryService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class AggregationBoxViewModelTest {

    private lateinit var subject: AggregationBoxViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var keywordSearch: KeywordArticleFinder

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { keywordSearch } bind (KeywordArticleFinder::class)
                    single(qualifier = null) { articlesReaderService } bind (ArticlesReaderService::class)
                }
            )
        }
        every { mainViewModel.initialAggregationType() } returns 0
        every { mainViewModel.switchAggregationBox(any()) } just Runs
        every { mainViewModel.showAggregationBox() } returns true
        every { articlesReaderService.invoke() } returns Stream.empty()

        mockkConstructor(InputHistoryService::class)
        every { anyConstructed<InputHistoryService>().add(any()) } just Runs
        every { anyConstructed<InputHistoryService>().clear(any()) } just Runs
        every { anyConstructed<InputHistoryService>().delete(any(), any()) } just Runs
        every { anyConstructed<InputHistoryService>().filter(any(), any()) } just Runs

        subject = AggregationBoxViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun focusingModifier() {
        subject.focusingModifier()
    }

    @Test
    fun onKeyEvent() {
        every { mainViewModel.switchAggregationBox(any()) } just Runs
        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                    java.awt.event.KeyEvent.VK_ESCAPE,
                    'A'
                )
            )
        )

        assertTrue(consumed)
        every { mainViewModel.switchAggregationBox(any()) }
    }

    @Test
    fun notConsumedOnKeyEvent() {
        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_RELEASED,
                    1,
                    java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                    java.awt.event.KeyEvent.VK_1,
                    'A'
                )
            )
        )

        assertFalse(consumed)
    }

    @Test
    fun switchAggregationBox() {
        every { mainViewModel.switchAggregationBox(any()) } just Runs

        subject.switchAggregationBox(true)

        verify { mainViewModel.switchAggregationBox(any()) }
    }

    @Test
    fun selectedCategoryName() {
        println(subject.selectedCategoryName())
    }

    @Test
    fun isNotCurrentSwingContent() {
        every { mainViewModel.currentTab() } returns mockk()

        assertFalse(subject.isCurrentSwingContent())
    }

    @Test
    fun isCurrentSwingContent() {
        every { mainViewModel.currentTab() } returns mockk<WebTab>()

        assertTrue(subject.isCurrentSwingContent())
    }

    @Test
    fun closeChooser() {
        assertFalse(subject.isOpeningChooser())

        subject.openChooser()

        assertTrue(subject.isOpeningChooser())

        subject.closeChooser()

        assertFalse(subject.isOpeningChooser())
    }

    @Test
    fun items() {
        assertFalse(subject.items().isEmpty())
    }

    @Test
    fun categories() {
        assertFalse(subject.categories().isEmpty())
    }

    @Test
    fun onKeywordValueChange() {
        assertTrue(subject.keyword().text.isEmpty())

        subject.onKeywordValueChange(TextFieldValue("new text"))

        assertEquals("new text", subject.keyword().text)

        subject.clearKeywordInput()

        assertTrue(subject.keyword().text.isEmpty())
    }

    @Test
    fun onDateValueChange() {
        assertTrue(subject.dateInput().text.isNotEmpty())

        subject.onDateInputValueChange(TextFieldValue("2023-12"))

        assertEquals("2023-12", subject.dateInput().text)

        subject.clearDateInput()

        assertTrue(subject.dateInput().text.isEmpty())
    }

    @Test
    fun choose() {
        subject.choose(subject.categories().entries.last())

        assertEquals("Find article", subject.selectedCategoryName())

        val result = mockk<AggregationResult>()
        every { result.isEmpty() } returns false
        every { result.title() } returns "test"
        every { keywordSearch.invoke(any(), any()) } returns result
        every { mainViewModel.openTab(any()) } just Runs

        subject.onSearch()

        assertSame(Modifier, subject.dateInputModifier())
        verify { keywordSearch.invoke(any(), any()) }
        verify { mainViewModel.openTab(any()) }
        verify { mainViewModel.switchAggregationBox(false) }
    }

    @Test
    fun onSearchWithBlankQuery() {
        subject.onDateInputValueChange(TextFieldValue("  "))

        subject.onSearch()

        verify(inverse = true) { mainViewModel.showSnackbar(any(), any(), any()) }
        verify(inverse = true) { mainViewModel.switchAggregationBox(any()) }
    }

    @Test
    fun onSearchReturnsEmptyResult() {
        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs

        subject.onSearch()

        verify { mainViewModel.showSnackbar(any(), any(), any()) }
        verify(inverse = true) { mainViewModel.switchAggregationBox(any()) }
    }

    @Test
    fun showWithShowAggregationBoxIsFalse() {
        every { mainViewModel.showAggregationBox() } returns false

        subject.start()

        verify { mainViewModel.showAggregationBox() }
    }

    @Test
    fun keywordHistory() {
        assertFalse(subject.shouldShowKeywordHistory())
        assertTrue(subject.keywordHistories().isEmpty())
    }

    @Test
    fun putKeyword() {
        subject.putKeyword(null)

        subject.putKeyword("test")

        val keyword = subject.keyword()
        assertEquals("test ", keyword.text)
        assertEquals(5, keyword.selection.start)
        assertEquals(5, keyword.selection.end)
    }

    @Test
    fun deleteInputHistoryItem() {
        subject.deleteInputHistoryItem("test")

        verify { anyConstructed<InputHistoryService>().delete(any(), any()) }
    }

    @Test
    fun clearKeywordHistory() {
        subject.clearKeywordHistory()

        verify { anyConstructed<InputHistoryService>().clear(any()) }
    }

    @Test
    fun dateHistories() {
        assertFalse(subject.shouldShowDateHistory())
        assertTrue(subject.dateHistories().isEmpty())
    }

    @Test
    fun putDate() {
        subject.putDate(null)

        subject.putDate("test")

        val textFieldValue = subject.dateInput()
        assertEquals("test ", textFieldValue.text)
        assertEquals(5, textFieldValue.selection.start)
        assertEquals(5, textFieldValue.selection.end)
    }

    @Test
    fun deleteDateHistoryItem() {
        subject.deleteDateHistoryItem("test")

        verify { anyConstructed<InputHistoryService>().delete(any(), any()) }
    }

    @Test
    fun clearDateHistory() {
        subject.clearDateHistory()

        verify { anyConstructed<InputHistoryService>().clear(any()) }
    }

}