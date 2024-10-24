package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.nio.file.Path
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_left_panel_close
import jp.toastkid.yobidashi4.presentation.main.component.AggregationBox
import jp.toastkid.yobidashi4.presentation.main.component.FindInPageBox
import jp.toastkid.yobidashi4.presentation.main.component.InputBox
import jp.toastkid.yobidashi4.presentation.main.component.MemoryUsageBox
import jp.toastkid.yobidashi4.presentation.main.component.WebSearchBox
import jp.toastkid.yobidashi4.presentation.time.WorldTimeView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MultiTabContent() {
    val viewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }

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

        if (viewModel.openMemoryUsageBox()) {
            MemoryUsageBox()
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    ArticleListView(viewModel.openArticleList(), viewModel.articles())

                    if (viewModel.openArticleList()) {
                        ArticleListSwitch(
                            viewModel::hideArticleList,
                            Modifier
                                .align(Alignment.CenterEnd)
                                .semantics { contentDescription = "Close file list." }
                        )
                    }
                }

                TabsView(modifier = Modifier.fillMaxHeight().weight(1f))
            }

            WorldTimeArea(viewModel.openWorldTime(), modifier = Modifier.align(Alignment.CenterEnd).wrapContentWidth(Alignment.End))
        }
    }

    LaunchedEffect(viewModel) {
        withContext(Dispatchers.IO) {
            viewModel.reloadAllArticle()
        }
    }
}

@Composable
private fun ArticleListView(openArticleList: Boolean, articles: List<Path>) {
    val width = animateDpAsState(if (openArticleList) 330.dp else 0.dp)

    if (articles.isNotEmpty()) {
        FileListView(
            articles,
            modifier = Modifier.widthIn(max = width.value).wrapContentWidth(Alignment.Start)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ArticleListSwitch(onClick: () -> Unit, modifier: Modifier) {
    val visibility = remember { mutableStateOf(false) }
    Icon(
        painterResource(Res.drawable.ic_left_panel_close),
        contentDescription = "Clear input.",
        tint = MaterialTheme.colors.secondary,
        modifier = modifier
            .alpha(animateFloatAsState(if (visibility.value) 1f else 0f).value)
            .onPointerEvent(PointerEventType.Enter) {
                visibility.value = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                visibility.value = false
            }
            .clickable(onClick = onClick)
    )
}

@Composable
private fun WorldTimeArea(open: Boolean, modifier: Modifier) {
    val width = animateDpAsState(if (open) 330.dp else 0.dp)

    WorldTimeView(
        modifier = modifier.widthIn(max = width.value)
    )
}
