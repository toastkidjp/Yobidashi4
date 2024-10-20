package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
internal fun MarkdownTabView(tab: MarkdownPreviewTab, modifier: Modifier) {
    val scrollState = rememberScrollState(tab.scrollPosition())
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        modifier = modifier.focusRequester(focusRequester)
    ) {
        MarkdownPreview(tab.markdown(), scrollState, modifier)

        DisposableEffect(tab) {
            focusRequester.requestFocus()
            coroutineScope.launch {
                scrollState.scrollTo(tab.scrollPosition())
            }

            onDispose {
                val mainViewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm

                mainViewModel.updateScrollableTab(tab, scrollState.value)
            }
        }
    }
}
