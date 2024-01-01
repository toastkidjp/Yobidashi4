package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.runtime.Composable
import jp.toastkid.yobidashi4.domain.model.tab.WebTab

@Composable
internal fun WebTabView(tab: WebTab) {
    if (tab.isReadableUrl().not()) {
        return
    }

    WebView(tab.id(), tab.url())
}