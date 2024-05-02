package jp.toastkid.yobidashi4.domain.service.article.finder

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult

interface FullTextArticleFinder {

    operator fun invoke(keyword: String): AggregationResult

}