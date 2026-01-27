package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun MarkdownTabView(tab: MarkdownPreviewTab, modifier: Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { MarkdownTabViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        modifier = modifier.focusRequester(viewModel.focusRequester())
    ) {
        MarkdownPreview(
            tab.markdown(),
            viewModel.scrollState(),
            modifier
        )

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
