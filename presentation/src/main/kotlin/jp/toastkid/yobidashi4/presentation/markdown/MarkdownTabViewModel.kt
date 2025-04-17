package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.focus.FocusRequester
import jp.toastkid.yobidashi4.domain.model.tab.ScrollableContentTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MarkdownTabViewModel : KoinComponent {

    private val scrollState = ScrollState(0)

    private val focusRequester = FocusRequester()

    private val mainViewModel: MainViewModel by inject()

    suspend fun launch(scrollPosition: Int) {
        scrollState.scrollTo(scrollPosition)
        focusRequester().requestFocus()
    }

    fun scrollState() = scrollState

    fun focusRequester() = focusRequester

    fun onDispose(tab: ScrollableContentTab) {
        mainViewModel.updateScrollableTab(tab, scrollState.value)
    }

}
