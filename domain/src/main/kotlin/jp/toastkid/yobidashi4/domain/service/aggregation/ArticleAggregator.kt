package jp.toastkid.yobidashi4.domain.service.aggregation

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult

interface ArticleAggregator {

    operator fun invoke(keyword: String): AggregationResult

    fun label(): String

}