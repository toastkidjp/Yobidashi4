package jp.toastkid.yobidashi4.presentation.input

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.tab.InputHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.ScrollableContentTab
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import jp.toastkid.yobidashi4.presentation.lib.input.InputHistoryService
import jp.toastkid.yobidashi4.presentation.main.component.AggregationInvoker
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference

class InputHistoryViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val keywordSearch: FullTextArticleFinder by inject()

    private val aggregator = AggregationInvoker()

    private val inputHistoryServiceHolder = AtomicReference<InputHistoryService>()

    private val items = mutableStateListOf<InputHistory>()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd(E)HH:mm:ss").withLocale(Locale.ENGLISH)

    private val listState = LazyListState()

    fun listState() = listState

    fun items(): List<InputHistory> = items

    fun open(inputHistory: InputHistory) {
        aggregator.invoke(keywordSearch, inputHistory.word)
    }

    fun openOnBackground(inputHistory: InputHistory) {
        aggregator.invoke(keywordSearch, inputHistory.word)
    }

    fun dateTimeString(inputHistory: InputHistory): String {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(inputHistory.timestamp), ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun launch(coroutineScope: CoroutineScope, tab: InputHistoryTab) {
        coroutineScope.launch {
            listState.scrollToItem(tab.scrollPosition())
        }

        val inputHistoryService = InputHistoryService(tab.category)
        inputHistoryService.all(items)
        inputHistoryServiceHolder.set(inputHistoryService)
    }

    fun onDispose(tab: ScrollableContentTab) {
        mainViewModel.updateScrollableTab(tab, listState.firstVisibleItemIndex)
    }

    fun delete(item: InputHistory) {
        inputHistoryServiceHolder.get().delete(items, item.word)
    }

}
