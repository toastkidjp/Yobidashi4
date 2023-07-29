package jp.toastkid.yobidashi4.domain.service.archive

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult

interface KeywordArticleFinder {
    operator fun invoke(keyword: String, fileFilter: String?): AggregationResult
}