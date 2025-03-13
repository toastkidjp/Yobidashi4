package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.main.content.sort.TableSorter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicReference

class TableViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val articleFactory: ArticleFactory by inject()

    private val articleStates = mutableStateListOf<Array<Any>>()

    private val lastSorted = AtomicReference(-1 to false)

    private val focusRequester = FocusRequester()

    private val state = LazyListState()

    private val scrollAction = KeyboardScrollAction(state)

    private val highlighter = KeywordHighlighter()

    private val query = mutableStateOf("")

    private val tableSorter = TableSorter()

    fun items() = articleStates

    fun focusRequester() = focusRequester

    fun listState() = state

    fun scrollAction(coroutineScope: CoroutineScope, key: Key, isCtrlPressed: Boolean): Boolean {
        return scrollAction.invoke(coroutineScope, key, isCtrlPressed)
    }

    suspend fun start(tab: TableTab) {
        articleStates.clear()
        val aggregationResult = tab.items()
        articleStates.addAll(aggregationResult.itemArrays())
        focusRequester().requestFocus()

        query.value = if (aggregationResult is FindResult) aggregationResult.keyword() else ""
        state.scrollToItem(tab.scrollPosition())
    }

    fun sort(index: Int, aggregationResult: AggregationResult) {
        val lastSort = this.lastSorted.get()
        val lastSortOrder = if (lastSort.first == index) lastSort.second else false
        this.lastSorted.set(index to lastSortOrder.not())

        tableSorter(lastSortOrder, aggregationResult, index, articleStates)
    }

    fun openMarkdownPreview(title: String) {
        val nextArticle = articleFactory.withTitle(title)
        mainViewModel.openPreview(nextArticle.path())
    }

    fun edit(title: String) {
        mainViewModel.editWithTitle(title)
    }

    fun highlight(text: String) = highlighter(text, query.value.replace("\"", ""))

    fun makeText(any: Any): String {
        return if (any is Int) String.format("%,d", any) else any.toString()
    }

    fun makeWeight(index: Int): Float {
        return if (index == 0) 0.4f else 1f
    }

    fun onDispose(tab: TableTab) {
        mainViewModel.updateScrollableTab(tab, state.firstVisibleItemIndex)
    }

}