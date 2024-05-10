package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TableViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val articleFactory: ArticleFactory by inject()

    private val articleStates = mutableStateListOf<Array<Any>>()

    private var lastSorted = -1 to false

    private val focusRequester = FocusRequester()

    private val state = LazyListState()

    private val scrollAction = KeyboardScrollAction(state)

    private val highlighter = KeywordHighlighter()

    private val query = mutableStateOf("")

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
        val lastSortOrder = if (lastSorted.first == index) lastSorted.second else false
        lastSorted = index to lastSortOrder.not()

        sort(lastSortOrder, aggregationResult, index, articleStates)
    }

    fun openMarkdownPreview(title: String) {
        val nextArticle = articleFactory.withTitle(title)
        mainViewModel.openPreview(nextArticle.path())
    }

    fun edit(title: String) {
        val nextArticle = articleFactory.withTitle(title)
        mainViewModel.edit(nextArticle.path())
    }

    private fun sort(
        lastSortOrder: Boolean,
        aggregationResult: AggregationResult,
        index: Int,
        articleStates: SnapshotStateList<Array<Any>>
    ) {
        val swap = if (lastSortOrder)
            if (aggregationResult.columnClass(index) == Int::class.java) {
                articleStates.sortedBy { it[index].toString().toIntOrNull() ?: 0 }
            } else if (aggregationResult.columnClass(index) == Double::class.java) {
                articleStates.sortedBy { it[index].toString().toDoubleOrNull() ?: 0.0 }
            } else {
                articleStates.sortedBy { it[index].toString() }
            }
        else
            if (aggregationResult.columnClass(index) == Int::class.java) {
                articleStates.sortedByDescending { it[index].toString().toIntOrNull() ?: 0 }
            } else if (aggregationResult.columnClass(index) == Double::class.java) {
                articleStates.sortedByDescending { it[index].toString().toDoubleOrNull() ?: 0.0 }
            } else {
                articleStates.sortedByDescending { it[index].toString() }
            }

        articleStates.clear()
        articleStates.addAll(swap)
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