package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.main.aggregation.AggregationBox
import jp.toastkid.yobidashi4.presentation.main.component.WebSearchBox
import jp.toastkid.yobidashi4.presentation.main.content.FileList
import jp.toastkid.yobidashi4.presentation.main.content.TabsView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MultiTabContent() {
    val viewModel = MainViewModel.get()

    Column {
        if (viewModel.showWebSearch()) {
            WebSearchBox(viewModel)
        }

        if (viewModel.showAggregationBox()) {
            AggregationBox(viewModel)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (viewModel.openArticleList()) {
                Box(modifier = Modifier.widthIn(max = 330.dp).wrapContentWidth(Alignment.Start)) {
                    ArticleListView(viewModel)
                    Text("x",
                        modifier = Modifier
                            .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                            .clickable { viewModel.switchArticleList() }
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            TabsView(modifier = Modifier.fillMaxHeight().weight(1f))
        }
    }

    LaunchedEffect(viewModel) {
        withContext(Dispatchers.IO) {
            viewModel.reloadAllArticle()
        }
    }
}

@Composable
private fun ArticleListView(viewModel: MainViewModel) {
    FileList(viewModel.articles())
}
