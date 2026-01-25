package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.markdown.Subhead
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_left_panel_open
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun MarkdownTabView(tab: MarkdownPreviewTab, modifier: Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { MarkdownTabViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        modifier = modifier.focusRequester(viewModel.focusRequester())
    ) {
        Box {
            Row {
                MarkdownPreview(tab.markdown(), viewModel.scrollState(), modifier.weight(1f))

                if (viewModel.showSubheadings()) {
                    MarkdownSubhead(tab.markdown().subheadings(), {
                        viewModel.scrollState().requestScrollToItem(it)
                    }, Modifier.weight(0.3f))
                }
            }

            val visibility = remember { mutableStateOf(false) }

            Icon(
                painterResource(Res.drawable.ic_left_panel_open),
                contentDescription = "Toggle subheadings.",
                tint = MaterialTheme.colors.secondary,
                modifier = modifier
                    .alpha(animateFloatAsState(if (visibility.value) 1f else 0f).value)
                    .onPointerEvent(PointerEventType.Enter) {
                        visibility.value = true
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        visibility.value = false
                    }
                    .clickable(onClick = viewModel::switchSubheadings)
                    .align(Alignment.CenterEnd)
            )
        }

        DisposableEffect(tab) {
            coroutineScope.launch {
                viewModel.launch(tab.scrollPosition())
            }

            onDispose {
                viewModel.onDispose(tab)
            }
        }
    }
}

@Composable
private fun MarkdownSubhead(
    subheadings: List<Subhead>,
    function: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            subheadings.forEach {
                Text(
                    text = it.text(),
                    fontSize = it.fontSize().sp,
                    lineHeight = it.fontSize().sp,
                    modifier = Modifier.fillMaxWidth().clickable {
                        function(it.indexOf())
                    }
                )
            }
        }

        VerticalScrollbar(
            rememberScrollbarAdapter(scrollState),
            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
        )
    }
}
