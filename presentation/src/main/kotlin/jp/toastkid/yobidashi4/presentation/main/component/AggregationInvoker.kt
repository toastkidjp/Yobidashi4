package jp.toastkid.yobidashi4.presentation.main.component

import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.service.aggregation.ArticleAggregator
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AggregationInvoker : KoinComponent {

    private val viewModel: MainViewModel by inject()

    fun invoke(aggregator: ArticleAggregator, query: String) {
        val result = aggregator.invoke(query.trim())
        if (result.isEmpty()) {
            viewModel.showSnackbar("Finding by \"$query\" has not get any result.")
            return
        }

        viewModel.openTab(TableTab(result.title(), result, true, reloadAction = { invoke(aggregator, query) }))

        viewModel.switchAggregationBox(false)
    }

}