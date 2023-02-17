package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebTabView(tab: WebTab) {
    val viewModel = remember {
        object : KoinComponent {
            val webTabViewModel: WebTabViewModel by inject()
        }.webTabViewModel
    }

    if (tab.isReadableUrl().not()) {
        return
    }

    viewModel.view(tab.id(), tab.url())
}
