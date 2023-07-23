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

    val browserView = remember { BrowserView() }

    if (tab.isReadableUrl().not()) {
        return
    }

    browserView.view(tab.id(), tab.url())

    LaunchedEffect(Unit) {
        receiveEvent(viewModel, browserView)
    }
}

private suspend fun receiveEvent(
    viewModel: WebTabViewModel,
    browserView: BrowserView
) {
    viewModel.event().collect {
        when (it) {
            is FindEvent -> {
                if (it.upward) {
                    browserView.findUp(it.id, it.text)
                } else {
                    browserView.find(it.id, it.text)
                }
            }

            is ReloadEvent -> {
                browserView.reload(it.id)
            }

            is SwitchDeveloperToolEvent -> {
                browserView.switchDevTools()
            }
        }
    }
}
