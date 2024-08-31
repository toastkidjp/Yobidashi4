package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.service.aggregation.ArticleAggregator
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AggregationBoxViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val keywordSearch: FullTextArticleFinder by inject()

    private val articlesReaderService: ArticlesReaderService by inject()

    private val focusRequester = FocusRequester()

    private val keyword = mutableStateOf(TextFieldValue())

    private val aggregations = listOf(
        MovieMemoSubtitleExtractor(articlesReaderService),
        StocksAggregatorService(articlesReaderService),
        OutgoAggregatorService(articlesReaderService),
        EatingOutCounterService(articlesReaderService),
        ArticleLengthAggregatorService(articlesReaderService),
        StepsAggregatorService(articlesReaderService),
        Nikkei225AggregatorService(articlesReaderService),
        keywordSearch
    )

    private val selectedSite = mutableStateOf(aggregations.get(
        when {
            viewModel.initialAggregationType() < 0 -> 0
            viewModel.initialAggregationType() >= aggregations.size -> aggregations.size - 1
            else -> viewModel.initialAggregationType()
        }
    ))

    private val query = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")).let {
        mutableStateOf(TextFieldValue(it, TextRange(it.length)))
    }

    private val openDropdown =  mutableStateOf(false)

    fun focusingModifier() = Modifier.focusRequester(focusRequester)

    fun onKeyEvent(it: KeyEvent): Boolean {
        if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
            viewModel.switchAggregationBox(false)
            return true
        }
        if (requireSecondInput() && it.type == KeyEventType.KeyDown && it.isCtrlPressed && it.key == Key.Two) {
            val newText = "\"${keyword.value.text}\""
            keyword.value = TextFieldValue(newText, TextRange(newText.length))
            return true
        }
        return false
    }

    fun switchAggregationBox(open: Boolean) {
        viewModel.switchAggregationBox(open)
    }

    fun selectedCategoryName(): String {
        return selectedSite.value.label()
    }

    fun selectedCategoryIcon(): String {
        return selectedSite.value.iconPath()
    }

    fun isCurrentSwingContent(): Boolean {
        return viewModel.currentTab() is WebTab
    }

    fun openChooser() {
        openDropdown.value = true
    }

    fun isOpeningChooser(): Boolean {
        return openDropdown.value
    }

    fun closeChooser() {
        openDropdown.value = false
    }

    fun items() = aggregations

    fun categories() = aggregations

    fun choose(articleAggregator: ArticleAggregator) {
        closeChooser()
        selectedSite.value = articleAggregator
    }

    fun requireSecondInput(): Boolean {
        return selectedSite.value is FullTextArticleFinder
    }

    fun keyword() = keyword.value

    fun onSearch() {
        invokeAggregation(viewModel, getQuery(), selectedSite.value)
    }

    private val keywordHistoryService: InputHistoryService = InputHistoryService("aggregation_keyword")

    private val keywordHistories = mutableStateListOf<InputHistory>()

    private val dateHistoryService: InputHistoryService = InputHistoryService("aggregation_date")

    private val dateHistories = mutableStateListOf<InputHistory>()

    fun shouldShowDateHistory(): Boolean =
        if (requireSecondInput()) keywordHistories.isNotEmpty() else dateHistories.isNotEmpty()

    fun dateHistories(): List<String> {
        return (if (requireSecondInput()) keywordHistories else dateHistories).map { it.word }
    }

    fun putDate(text: String?) {
        if (requireSecondInput()) {
            val newFieldValue = keywordHistoryService.make(text) ?: return
            keyword.value = newFieldValue
            keywordHistories.clear()
            return
        }
        val newFieldValue = dateHistoryService.make(text) ?: return
        query.value = newFieldValue
        dateHistories.clear()
        keywordHistories.clear()
    }

    fun deleteDateHistoryItem(text: String) {
        if (requireSecondInput()) {
            keywordHistoryService.delete(keywordHistories, text)
            return
        }

        dateHistoryService.delete(dateHistories, text)
    }

    fun clearDateHistory() {
        if (requireSecondInput()) {
            keywordHistoryService.clear(keywordHistories)
            return
        }

        dateHistoryService.clear(dateHistories)
    }

    private fun invokeAggregation(
        viewModel: MainViewModel,
        query: String,
        aggregator: ArticleAggregator
    ) {
        if (query.isBlank()) {
            return
        }

        if (requireSecondInput()) {
            keywordHistoryService.add(query)
        } else {
            dateHistoryService.add(query)
        }

        val result = aggregator.invoke(query.trim())
        if (result.isEmpty()) {
            viewModel.showSnackbar("Finding by \"$query\" has not get any result.")
            return
        }

        viewModel.openTab(TableTab(result.title(), result, true, reloadAction = { invokeAggregation(viewModel, query, aggregator) }))

        viewModel.switchAggregationBox(false)
    }

    private fun getQuery() = if (requireSecondInput()) keyword.value.text else query.value.text

    fun showAggregationBox(): Boolean {
        return viewModel.showAggregationBox()
    }

    fun start() {
        if (viewModel.showAggregationBox()) {
            focusRequester.requestFocus()
        }
    }

    fun dateInput() = if (requireSecondInput()) keyword.value else query.value

    fun label() = if (requireSecondInput()) "Keyword" else "Year-Month"

    fun onDateInputValueChange(it: TextFieldValue) {
        if (requireSecondInput()) {
            onKeywordValueChange(it)
            return
        }

        query.value = it

        dateHistoryService.filter(dateHistories, it.text)
    }

    private fun onKeywordValueChange(it: TextFieldValue) {
        keyword.value = it

        keywordHistoryService.filter(keywordHistories, it.text)
    }

    fun clearDateInput() {
        if (requireSecondInput()) {
            keyword.value = TextFieldValue()
            return
        }

        query.value = TextFieldValue()
    }

}