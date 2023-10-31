package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import javax.swing.JDialog
import javax.swing.WindowConstants
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import jp.toastkid.yobidashi4.presentation.web.event.FindEvent
import jp.toastkid.yobidashi4.presentation.web.event.ReloadEvent
import jp.toastkid.yobidashi4.presentation.web.event.SwitchDeveloperToolEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
internal fun WebTabView(tab: WebTab) {
    if (tab.isReadableUrl().not()) {
        return
    }

    WebView(tab.id(), tab.url())

    LaunchedEffect(Unit) {
        receiveEvent()
    }
}

private suspend fun receiveEvent() {
    val webViewPool = object : KoinComponent { val pool: WebViewPool by inject() }.pool

    object : KoinComponent { val webTabViewModel: WebTabViewModel by inject() }
        .webTabViewModel
        .event()
        .collect {
            when (it) {
                is FindEvent -> {
                    if (it.upward) {
                        webViewPool.find(it.id, it.text, false)
                    } else {
                        webViewPool.find(it.id, it.text, true)
                    }
                }

                is ReloadEvent -> {
                    webViewPool.reload(it.id)
                }

                is SwitchDeveloperToolEvent -> {
                    val devToolsDialog = JDialog()
                    devToolsDialog.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
                    devToolsDialog.setSize(800, 600)
                    devToolsDialog.add(webViewPool.devTools(it.id))
                    devToolsDialog.isVisible = true
                }
            }
        }
}
