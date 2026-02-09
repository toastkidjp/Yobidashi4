/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.aggregation

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService

class EatingOutCounterService(articlesReaderService: ArticlesReaderService) : ArticleAggregator {

    private val behavior = OutgoCalculationBehavior(articlesReaderService, ::contains)

    private fun contains(it: String): Boolean {
        return it.contains(TARGET_LINE_LABEL).not()
    }

    override operator fun invoke(keyword: String): AggregationResult {
        return behavior.invoke(keyword)
    }

    override fun label() = "Eat out"

}

private const val TARGET_LINE_LABEL = "(外食)"
