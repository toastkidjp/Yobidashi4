package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import java.awt.Component

@Factory
class WebTabViewModel(
    private val mainViewModel: MainViewModel,
    private val webViewPool: WebViewPool
) : KoinComponent {

    private val focusRequester = FocusRequester()

    fun component(tab: WebTab): Component {
        return webViewPool.component(tab.id(), tab.url())
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