package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.service.aggregation.ArticleLengthAggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.EatingOutCounterService
import jp.toastkid.yobidashi4.domain.service.aggregation.MovieMemoSubtitleExtractor
import jp.toastkid.yobidashi4.domain.service.aggregation.Nikkei225AggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.OutgoAggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.StepsAggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.StocksAggregatorService
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import jp.toastkid.yobidashi4.presentation.lib.input.InputHistoryService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.stream.Stream

@OptIn(InternalComposeUiApi::class)
class AggregationBoxViewModelTest {

    private lateinit var subject: AggregationBoxViewModel

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
        every { mainViewModel.switchAggregationBox(any()) } just Runs
        every { mainViewModel.showAggregationBox() } returns true
        every { articlesReaderService.invoke() } answers { Stream.empty() }

        mockkConstructor(InputHistoryService::class, AggregationInvoker::class)
        every { anyConstructed<InputHistoryService>().add(any()) } just Runs
        every { anyConstructed<InputHistoryService>().clear(any()) } just Runs
        every { anyConstructed<InputHistoryService>().delete(any(), any()) } just Runs
        every { anyConstructed<InputHistoryService>().filter(any(), any()) } just Runs
        every { anyConstructed<AggregationInvoker>().invoke(any(), any()) } just Runs

        every { keywordSearch.label() } returns "Find article"

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
            KeyEvent(Key.Escape, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertTrue(consumed)
        every { mainViewModel.switchAggregationBox(any()) }
    }

    @Test
    fun onKeyEventWith2Key() {
        subject.choose(subject.categories().last())
        subject.dateInput().setTextAndPlaceCursorAtEnd("test")
        subject.onDateInputValueChange()

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Two, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertTrue(consumed)
        assertEquals("\"test\"", subject.dateInput().text)
    }

    @Test
    fun onKeyEventWith2KeyButKeyHasReleased() {
        subject.choose(subject.categories().last())
        subject.dateInput().setTextAndPlaceCursorAtEnd("test")
        subject.onDateInputValueChange()

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Two, KeyEventType.KeyUp, isCtrlPressed = true)
        )

        assertFalse(consumed)
    }

    @Test
    fun onKeyEventWith2KeyButCurrentIsNotFind() {
        subject.dateInput().setTextAndPlaceCursorAtEnd("test")
        subject.onDateInputValueChange()

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Two, KeyEventType.KeyDown, isCtrlPressed = true)
        )

        assertFalse(consumed)
    }


    @Test
    fun unconsumedOnKeyEventWith2KeyAndOtherMask() {
        subject.choose(subject.categories().last())
        subject.dateInput().setTextAndPlaceCursorAtEnd("test")
        subject.onDateInputValueChange()

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Two, KeyEventType.KeyDown, isShiftPressed = true)
        )

        assertFalse(consumed)
        assertEquals("test", subject.dateInput().text)
    }

    @Test
    fun notConsumedOnKeyEvent() {
        val consumed = subject.onKeyEvent(
            KeyEvent(Key.One, KeyEventType.KeyUp, isCtrlPressed = true)
        )

        assertFalse(consumed)
    }

    @Test
    fun notConsumedOnKeyEventWithEscapeReleased() {
        every { mainViewModel.switchAggregationBox(any()) } just Runs

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Escape, KeyEventType.KeyUp, isCtrlPressed = true)
        )

        assertFalse(consumed)
        verify(inverse = true) { mainViewModel.switchAggregationBox(any()) }
    }

    @Test
    fun switchAggregationBox() {
        every { mainViewModel.switchAggregationBox(any()) } just Runs

        subject.switchAggregationBox(true)

        verify { mainViewModel.switchAggregationBox(any()) }
    }

    @Test
    fun selectedCategoryName() {
        assertTrue(subject.selectedCategoryName().isNotEmpty())
    }

    @Test
    fun selectedCategoryIcon() {
        assertNotNull(subject.selectedCategoryIcon())
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
        subject.choose(subject.categories().last())

        assertTrue(subject.keyword().text.isEmpty())

        subject.dateInput().setTextAndPlaceCursorAtEnd("new text")
        subject.onDateInputValueChange()

        assertEquals("new text", subject.keyword().text)

        subject.clearDateInput()

        assertTrue(subject.keyword().text.isEmpty())
    }

    @Test
    fun onDateValueChange() {
        assertTrue(subject.dateInput().text.isNotEmpty())

        subject.dateInput().setTextAndPlaceCursorAtEnd("2023-12")
        subject.onDateInputValueChange()

        assertEquals("2023-12", subject.dateInput().text)

        subject.clearDateInput()

        assertTrue(subject.dateInput().text.isEmpty())
    }

    @Test
    fun choose() {
        subject.choose(subject.categories().last())

        assertEquals("Find article", subject.selectedCategoryName())

        subject.dateInput().setTextAndPlaceCursorAtEnd("test")
        subject.onDateInputValueChange()

        subject.onSearch()

        verify { anyConstructed<AggregationInvoker>().invoke(any(), any()) }
    }

    @Test
    fun requireSecondInput() {
        assertFalse(subject.requireSecondInput())

        subject.choose(subject.categories().last())

        assertTrue(subject.requireSecondInput())
    }

    @ParameterizedTest
    @CsvSource(
        "false, \"Test, \"Test",
        "false, Test\", Test\"",
        "false, \"Test\", \"Test\"",
        "true, \"Test, \"Test",
        "true, Test\", Test\"",
        "true, \"Test\", \"Test\"",
        "true, Test, Test"
    )
    fun onSearchWithCheckingExactmatchBehavior(setExactMatchToFalse: Boolean, input: String, expected: String) {
        if (setExactMatchToFalse) {
            subject.setExactMatch(false)
        }

        subject.dateInput().setTextAndPlaceCursorAtEnd(input)
        subject.onDateInputValueChange()

        val capturingSlot = slot<String>()

        subject.onSearch()

        verify(inverse = true) { mainViewModel.switchAggregationBox(any()) }
        verify { anyConstructed<AggregationInvoker>().invoke(any(), capture(capturingSlot)) }
        assertEquals(expected, capturingSlot.captured)
    }

    @Test
    fun onSearchWithBlankQuery() {
        subject.dateInput().setTextAndPlaceCursorAtEnd("  ")
        subject.onDateInputValueChange()

        subject.onSearch()

        verify(inverse = true) { mainViewModel.switchAggregationBox(any()) }
        verify(inverse = true) { anyConstructed<AggregationInvoker>().invoke(any(), any()) }
    }

    @Test
    fun onSearchReturnsEmptyResult() {
        subject.onSearch()

        verify(inverse = true) { mainViewModel.switchAggregationBox(any()) }
        verify { anyConstructed<AggregationInvoker>().invoke(any(), any()) }
    }

    @Test
    fun showWithShowAggregationBoxIsFalse() {
        every { mainViewModel.showAggregationBox() } returns false

        subject.start()

        verify { mainViewModel.showAggregationBox() }
    }

    @Test
    fun dateHistories() {
        assertFalse(subject.shouldShowDateHistory())
        assertTrue(subject.dateHistories().isEmpty())
    }

    @Test
    fun keywordHistory() {
        subject.choose(subject.categories().last())

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
    fun putKeyword() {
        subject.choose(subject.categories().last())

        subject.putDate(null)

        subject.putDate("test")

        val keyword = subject.keyword()
        assertEquals("test ", keyword.text)
        assertEquals(5, keyword.selection.start)
        assertEquals(5, keyword.selection.end)
    }

    @Test
    fun deleteDateHistoryItem() {
        subject.deleteDateHistoryItem("test")

        verify { anyConstructed<InputHistoryService>().delete(any(), any()) }
    }

    @Test
    fun deleteInputHistoryItem() {
        subject.choose(subject.categories().last())

        subject.deleteDateHistoryItem("test")

        verify { anyConstructed<InputHistoryService>().delete(any(), any()) }
    }

    @Test
    fun clearDateHistory() {
        subject.clearDateHistory()

        verify { anyConstructed<InputHistoryService>().clear(any()) }
    }

    @Test
    fun clearKeywordHistory() {
        subject.choose(subject.categories().last())

        subject.clearDateHistory()

        verify { anyConstructed<InputHistoryService>().clear(any()) }
    }

    @Test
    fun label() {
        val normal = subject.label()
        subject.choose(subject.categories().last())
        val find = subject.label()

        assertNotEquals(normal, find)
    }
    
    @Test
    fun icon () {
        val aggregators = setOf(
            mockk(),
            mockk<ArticleLengthAggregatorService>(),
            mockk<EatingOutCounterService>(),
            mockk<FullTextArticleFinder>(),
            mockk<MovieMemoSubtitleExtractor>(),
            mockk<Nikkei225AggregatorService>(),
            mockk<OutgoAggregatorService>(),
            mockk<StepsAggregatorService>(),
            mockk<StocksAggregatorService>()
        )

        assertEquals(aggregators.size, aggregators.map(subject::icon).size)
    }

    @Test
    fun openHistory() {
        every { mainViewModel.openInputHistory(any()) } just Runs

        subject.openHistory()

        verify { mainViewModel.openInputHistory(any()) }
    }

}