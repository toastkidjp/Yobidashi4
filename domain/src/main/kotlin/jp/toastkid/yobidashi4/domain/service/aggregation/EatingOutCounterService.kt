package jp.toastkid.yobidashi4.domain.service.aggregation

import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService

class EatingOutCounterService(articlesReaderService: ArticlesReaderService) {

    private val behavior = OutgoCalculationBehavior(articlesReaderService, { it.contains(TARGET_LINE_LABEL).not() })

    operator fun invoke(keyword: String): OutgoAggregationResult {
        return behavior.invoke(keyword)
    }

}

private const val TARGET_LINE_LABEL = "(外食)"
