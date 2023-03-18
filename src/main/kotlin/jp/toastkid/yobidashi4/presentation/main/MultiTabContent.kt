package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.main.component.AggregationBox
import jp.toastkid.yobidashi4.presentation.main.component.FindInPageBox
import jp.toastkid.yobidashi4.presentation.main.component.InputBox
import jp.toastkid.yobidashi4.presentation.main.component.WebSearchBox
import jp.toastkid.yobidashi4.presentation.main.content.FileList
import jp.toastkid.yobidashi4.presentation.main.content.TabsView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun MultiTabContent() {
    val viewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm

    Column {
        if (viewModel.showWebSearch()) {
            WebSearchBox()
        }

        if (viewModel.showAggregationBox()) {
            AggregationBox()
        }

        if (viewModel.openFind()) {
            FindInPageBox()
        }

        if (viewModel.showInputBox()) {
            InputBox()
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            ArticleListView()

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
private fun ArticleListView() {
    val viewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }

    val width = animateDpAsState(if (viewModel.openArticleList()) 330.dp else 0.dp)

    if (viewModel.articles().isNotEmpty()) {
        Box(modifier = Modifier.widthIn(max = width.value).wrapContentWidth(Alignment.Start)) {
            FileList(viewModel.articles())
        }
    }
}
