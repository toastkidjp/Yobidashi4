package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import jp.toastkid.yobidashi4.presentation.web.event.FindEvent
import jp.toastkid.yobidashi4.presentation.web.event.ReloadEvent
import jp.toastkid.yobidashi4.presentation.web.event.SwitchDeveloperToolEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebTabView(tab: WebTab) {
    val viewModel = remember {
        object : KoinComponent {
            val webTabViewModel: WebTabViewModel by inject()
        }.webTabViewModel
    }

    val webViewProvider = remember { WebViewProvider() }

    if (tab.isReadableUrl().not()) {
        return
    }

    webViewProvider.view(tab.id(), tab.url())

    LaunchedEffect(Unit) {
        receiveEvent(viewModel, webViewProvider)
    }
}

private suspend fun receiveEvent(
    viewModel: WebTabViewModel,
    webViewProvider: WebViewProvider
) {
    viewModel.event().collect {
        when (it) {
            is FindEvent -> {
                if (it.upward) {
                    webViewProvider.findUp(it.id, it.text)
                } else {
                    webViewProvider.find(it.id, it.text)
                }
            }

            is ReloadEvent -> {
                webViewProvider.reload(it.id)
            }

            is SwitchDeveloperToolEvent -> {
                webViewProvider.switchDevTools(it.id)
            }
        }
    }
}
