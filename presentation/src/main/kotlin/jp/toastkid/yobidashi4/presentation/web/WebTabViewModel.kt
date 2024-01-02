package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import java.awt.Color
import java.awt.Component
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.editor.finder.FindOrder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WebTabViewModel : KoinComponent {

    private val focusRequester = FocusRequester()

    private val mainViewModel: MainViewModel by inject()

    private val webViewPool: WebViewPool by inject()

    fun component(tab: WebTab, background: Color): Component {
        val component = webViewPool.component(tab.id(), tab.url())
        component.background = background
        component.setSize(1281, 1040)
        component.requestFocus()
        return component
    }

    fun focusRequester() = focusRequester

    fun spacerHeight() =
        if (mainViewModel.showingSnackbar()) 48.dp else 0.dp

    suspend fun start(id: String) {
        focusRequester().requestFocus()

        mainViewModel.finderFlow().collect {
            if (it == FindOrder.EMPTY) {
                webViewPool.clearFind(id)
            } else {
                webViewPool.find(id, it.target, it.upper.not())
            }
        }
    }

}