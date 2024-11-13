package jp.toastkid.yobidashi4.domain.service.aggregation

import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService

class OutgoAggregatorService(private val articlesReaderService: ArticlesReaderService) : ArticleAggregator {

    private val service = OutgoCalculationBehavior(articlesReaderService, { false })

    override operator fun invoke(keyword: String): OutgoAggregationResult {
        return service.invoke(keyword)
    }

    override fun label() = "Outgo"

}