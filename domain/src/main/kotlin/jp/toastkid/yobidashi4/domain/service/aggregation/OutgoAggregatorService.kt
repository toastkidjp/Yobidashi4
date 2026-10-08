/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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