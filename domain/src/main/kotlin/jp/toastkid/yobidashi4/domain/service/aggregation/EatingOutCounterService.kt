package jp.toastkid.yobidashi4.domain.service.aggregation

import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService

class EatingOutCounterService(
    articlesReaderService: ArticlesReaderService,
    additionalLineFilter: (String) -> Boolean = { it.contains(TARGET_LINE_LABEL).not() }
) {

    private val behavior = OutgoCalculationBehavior(articlesReaderService, additionalLineFilter)

    operator fun invoke(keyword: String): OutgoAggregationResult {
        return behavior.invoke(keyword)
    }

}

private const val TARGET_LINE_LABEL = "(外食)"
