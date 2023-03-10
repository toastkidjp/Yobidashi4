package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableView(aggregationResult: AggregationResult) {
    val articleStates = mutableStateListOf<Array<Any>>()
    articleStates.addAll(aggregationResult.itemArrays())

    var lastSorted = remember { -1 to false }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box {
            val state = rememberLazyListState()
            val horizontalScrollState = rememberScrollState()
            LazyColumn(
                state = state,
                userScrollEnabled = true
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(if (state.firstVisibleItemIndex != 0) MaterialTheme.colors.surface else Color.Transparent)
                    ) {
                        aggregationResult.header().forEachIndexed { index, item ->
                            if (index != 0) {
                                Divider(modifier = Modifier.fillMaxHeight().width(1.dp).padding(vertical = 1.dp))
                            }

                            Text(
                                item.toString(),
                                modifier = Modifier
                                    .clickable {
                                        val lastSortOrder = if (lastSorted.first == index) lastSorted.second else false
                                        lastSorted = index to lastSortOrder.not()

                                        sort(lastSortOrder, aggregationResult, index, articleStates)
                                    }
                                    .weight(if (index == 0) 0.4f else 1f)
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
                }

                items(articleStates) { article ->
                    SelectionContainer {
                        Column(modifier = Modifier.animateItemPlacement()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(
                                    painter = painterResource("images/icon/ic_edit.xml"),
                                    contentDescription = "Open file",
                                    tint = MaterialTheme.colors.secondary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                        .clickable {
                                            // TODO get main vm with koin.
                                            val nextArticle = object : KoinComponent { val articleFactory: ArticleFactory by inject() }
                                                .articleFactory.withTitle(article[0].toString())
                                            object : KoinComponent { val vm: MainViewModel by inject() }.vm.openFile(nextArticle.path())
                                        }
                                )

                                article.forEachIndexed { index, any ->
                                    if (index != 0) {
                                        Divider(modifier = Modifier.fillMaxHeight().width(1.dp).padding(vertical = 1.dp))
                                    }
                                    if (any is Collection<*>) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            any.forEach { line ->
                                                Text(
                                                    line.toString(),
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp)
                                                )
                                                Divider(modifier = Modifier.padding(start = 8.dp))
                                            }
                                        }
                                        return@forEachIndexed
                                    }
                                    Text(
                                        any.toString(),
                                        modifier = Modifier
                                            .weight(if (index == 0) 0.4f else 1f)
                                            .padding(horizontal = 16.dp)
                                    )
                                }
                            }
                            Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
                        }
                    }
                }
            }
            VerticalScrollbar(adapter = rememberScrollbarAdapter(state), modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd))
            HorizontalScrollbar(adapter = rememberScrollbarAdapter(horizontalScrollState), modifier = Modifier.fillMaxWidth().align(
                Alignment.BottomCenter))
        }
    }
}

private fun sort(
    lastSortOrder: Boolean,
    aggregationResult: AggregationResult,
    index: Int,
    articleStates: SnapshotStateList<Array<Any>>
) {
    val swap = if (lastSortOrder)
        if (aggregationResult.columnClass(index) == Integer::class.java) {
            articleStates.sortedBy { it[index].toString().toIntOrNull() ?: 0 }
        } else {
            articleStates.sortedBy { return@sortedBy it[index].toString() }
        }
    else
        if (aggregationResult.columnClass(index) == Integer::class.java) {
            articleStates.sortedByDescending { it[index].toString().toIntOrNull() ?: 0 }
        } else {
            articleStates.sortedByDescending { it[index].toString() }
        }

    articleStates.clear()
    articleStates.addAll(swap)
}