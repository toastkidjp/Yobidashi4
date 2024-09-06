package jp.toastkid.yobidashi4.domain.service.aggregation

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService

class EatingOutCounterService(articlesReaderService: ArticlesReaderService) : ArticleAggregator {

    private val behavior = OutgoCalculationBehavior(articlesReaderService, { it.contains(TARGET_LINE_LABEL).not() })

    override operator fun invoke(keyword: String): AggregationResult {
        return behavior.invoke(keyword)
    }

    override fun label() = "Eat out"

    override fun iconPath() = "images/icon/ic_restaurant.xml"

}

private const val TARGET_LINE_LABEL = "(外食)"
